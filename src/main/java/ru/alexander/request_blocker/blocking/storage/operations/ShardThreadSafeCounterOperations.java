package ru.alexander.request_blocker.blocking.storage.operations;

import ru.alexander.request_blocker.blocking.ip.api.exceptions.ExecutionBlockException;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.api.locks.StorageLockAccess;
import ru.alexander.request_blocker.blocking.storage.api.locks.StorageLockHandle;

public class ShardThreadSafeCounterOperations extends SimpleCounterStorageOperations {
    private final StorageLockAccess storageLock;
    private final StorageLockHandle storageAccessHandle;

    public ShardThreadSafeCounterOperations(
        StorageLockAccess storageLock,
        StorageLockHandle storageAccessHandle,
        CountersStorage storage,
        int requestsLimit) {
        super(storage, requestsLimit);
        this.storageLock = storageLock;
        this.storageAccessHandle = storageAccessHandle;
    }

    @Override
    public void validateIPCount(int executionID, String ip) throws ExecutionBlockException {
        storageLock.lockAccess(executionID, ip);
        super.validateIPCount(executionID, ip);
        storageLock.unlockAccess(executionID, ip);
    }

    @Override
    public void clearStorage() {
        storageAccessHandle.lock();
        super.clearStorage();
        storageAccessHandle.release();
    }
}
