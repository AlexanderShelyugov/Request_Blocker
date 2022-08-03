package ru.alexander.request_blocker.blocking.storage.operations;

import ru.alexander.request_blocker.blocking.ip.api.exceptions.ExecutionBlockException;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.api.locks.ShardLock;
import ru.alexander.request_blocker.blocking.storage.api.locks.StorageLock;

public class ShardThreadSafeCounterOperations extends SimpleCounterStorageOperations {
    private final ShardLock shardLock;
    private final StorageLock storageLock;

    public ShardThreadSafeCounterOperations(
        ShardLock shardLock,
        StorageLock storageLock,
        CountersStorage storage,
        int requestsLimit) {
        super(storage, requestsLimit);
        this.shardLock = shardLock;
        this.storageLock = storageLock;
    }

    @Override
    public void validateIPCount(int executionID, String ip) throws ExecutionBlockException {
        shardLock.lock(executionID, ip);
        try {
            super.validateIPCount(executionID, ip);
        } finally {
            shardLock.unlock(executionID, ip);
        }
    }

    @Override
    public void clearStorage() {
        storageLock.lock();
        try {
            super.clearStorage();
        } finally {
            storageLock.unlock();
        }
    }
}
