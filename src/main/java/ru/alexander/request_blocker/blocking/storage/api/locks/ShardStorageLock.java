package ru.alexander.request_blocker.blocking.storage.api.locks;

import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.alexander.request_blocker.blocking.storage.sharding.ShardingStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ShardStorageLock implements ShardLock, StorageLock {
    private final AtomicBoolean storageIsOpen = new AtomicBoolean(true);
    private final AtomicLong activeStorageUses = new AtomicLong(0);
    private final Object shardAccess = new Object();
    private final Object storageAccess = new Object();
    private final Map<String, Lock> shardLocks = new HashMap<>();
    private final ShardingStrategy shardingStrategy;

    @Override
    public void lock() {
        // We stop letting new users in
        synchronized (storageAccess) {
            storageIsOpen.set(false);
        }

        // But we are waiting for everyone who works with the repository to finish their work
        long activeUsers = Long.MAX_VALUE;
        while (0 < activeUsers) {
            synchronized (storageAccess) {
                activeUsers = activeStorageUses.get();
                if (activeUsers < 0) {
                    throw new IllegalStateException("Impossible negative number of active storage users!");
                }
            }
            Thread.yield();
        }
    }

    @Override
    public void unlock() {
        synchronized (storageAccess) {
            if (0 != activeStorageUses.get()) {
                throw new IllegalStateException("Tried to lock storage. There were active storage uses!");
            }
            storageIsOpen.set(true);
        }
    }

    @Override
    public void lock(int executionID, String ip) {
        synchronized (shardAccess) {
            registerStorageUsage();
            try {
                val shardName = shardingStrategy.getShardName(executionID, ip);
                val shardLock = shardLocks.computeIfAbsent(shardName, name -> new ReentrantLock());
                shardLock.lock();
            } catch (Throwable e) {
                unregisterStorageUsage();
                throw e;
            }
        }
    }

    @Override
    public void unlock(int executionID, String ip) {
        synchronized (shardAccess) {
            val shardName = shardingStrategy.getShardName(executionID, ip);
            val shardLock = requireNonNull(shardLocks.get(shardName), "Failed to achieve lock.");
            shardLock.unlock();
            unregisterStorageUsage();
        }
    }

    private void registerStorageUsage() {
        while (true) {
            synchronized (storageAccess) {
                if (storageIsOpen.get()) {
                    activeStorageUses.incrementAndGet();
                    return;
                }
            }
            Thread.yield();
        }
    }

    private void unregisterStorageUsage() {
        synchronized (storageAccess) {
            val u = activeStorageUses.decrementAndGet();
            if (u < 0) {
                throw new IllegalStateException("Number of registrations do not match!");
            }
        }
    }
}
