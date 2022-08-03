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
public class ShardStorageLock implements StorageLockAccess, StorageLockHandle {
    private final AtomicBoolean storageIsOpen = new AtomicBoolean(true);
    private final Object storageShardLock = new Object();
    private final Object globalStorageLock = new Object();
    private final AtomicLong activeStorageUses = new AtomicLong(0);
    private final Map<String, Lock> shardLocks = new HashMap<>();
    private final ShardingStrategy shardingStrategy;

    @Override
    public boolean canAccessStorage() {
        return storageIsOpen.get();
    }

    @Override
    public void lock() {
        while (true) {
            synchronized (globalStorageLock) {
                if (0 < activeStorageUses.get()) {
                    Thread.yield();
                    continue;
                }
                storageIsOpen.set(false);
                return;
            }
        }
    }

    @Override
    public void release() {
        synchronized (globalStorageLock) {
            if (0 != activeStorageUses.get()) {
                throw new IllegalStateException("Tried to lock storage. There were active storage uses!");
            }
            storageIsOpen.set(true);
        }
    }

    @Override
    public void lockAccess(int executionID, String ip) {
        synchronized (storageShardLock) {
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
    public void unlockAccess(int executionID, String ip) {
        synchronized (storageShardLock) {
            val shardName = shardingStrategy.getShardName(executionID, ip);
            val shardLock = requireNonNull(shardLocks.get(shardName), "Failed to achieve lock.");
            shardLock.unlock();
            unregisterStorageUsage();
        }
    }

    private void registerStorageUsage() {
        while (true) {
            synchronized (globalStorageLock) {
                if (!storageIsOpen.get()) {
                    Thread.yield();
                    continue;
                }
                activeStorageUses.incrementAndGet();
                return;
            }
        }
    }

    private void unregisterStorageUsage() {
        synchronized (globalStorageLock) {
            activeStorageUses.decrementAndGet();
        }
    }
}
