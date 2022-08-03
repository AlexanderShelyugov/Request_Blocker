package ru.alexander.request_blocker.blocking.storage.api.locks;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.val;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.ShardingStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor
public class ShardStorageLock implements ShardLock, StorageLock {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock storageLock = readWriteLock.writeLock();
    private final Lock shardAccessLock = readWriteLock.readLock();
    private final Map<String, Lock> shardLocks = new HashMap<>();
    private final ShardingStrategy shardingStrategy;

    @Override
    public void lock() {
        storageLock.lock();
    }

    @Override
    public void unlock() {
        storageLock.unlock();
    }

    @Override
    public void lock(int executionID, String ip) {
        shardAccessLock.lock();
        try {
            val shardLock = getShardLock(executionID, ip);
            shardLock.lock();
        } finally {
            shardAccessLock.unlock();
        }
    }

    @Override
    public void unlock(int executionID, String ip) {
        shardAccessLock.lock();
        try {
            val shardLock = getShardLock(executionID, ip);
            shardLock.unlock();
        } finally {
            shardAccessLock.unlock();
        }
    }

    @Synchronized
    private Lock getShardLock(int executionID, String ip) {
        val shardName = shardingStrategy.getShardName(executionID, ip);
        return shardLocks.computeIfAbsent(shardName, shard -> new ReentrantLock());
    }
}
