package ru.alexander.request_blocker.blocking.storage.api.locks;

import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.alexander.request_blocker.blocking.storage.sharding.ShardingStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RequiredArgsConstructor
public class ShardStorageLock implements ShardLock, StorageLock {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Map<String, Lock> shardLocks = new HashMap<>();
    private final ShardingStrategy shardingStrategy;

    @Override
    public void lock() {
        readWriteLock.writeLock().lock();
    }

    @Override
    public void unlock() {
        readWriteLock.writeLock().unlock();
    }

    @Override
    public void lock(int executionID, String ip) {
        readWriteLock.readLock().lock();
        try {
            val shardLock = getShardLock(executionID, ip);
            shardLock.lock();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void unlock(int executionID, String ip) {
        readWriteLock.readLock().lock();
        try {
            val shardLock = getShardLock(executionID, ip);
            shardLock.unlock();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private Lock getShardLock(int executionID, String ip) {
        val shardName = shardingStrategy.getShardName(executionID, ip);
        return shardLocks.computeIfAbsent(shardName, shard -> new ReentrantLock());
    }
}
