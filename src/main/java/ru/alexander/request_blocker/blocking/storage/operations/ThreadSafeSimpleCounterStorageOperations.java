package ru.alexander.request_blocker.blocking.storage.operations;

import lombok.Synchronized;
import ru.alexander.request_blocker.blocking.ip.api.exceptions.ExecutionBlockException;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;

public class ThreadSafeSimpleCounterStorageOperations extends SimpleCounterStorageOperations {
    public ThreadSafeSimpleCounterStorageOperations(CountersStorage storage, int requestsLimit) {
        super(storage, requestsLimit);
    }

    @Override
    @Synchronized
    public void validateIPCount(int executionID, String ip) throws ExecutionBlockException {
        super.validateIPCount(executionID, ip);
    }

    @Override
    @Synchronized
    public void clearStorage() {
        super.clearStorage();
    }
}
