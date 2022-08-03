package ru.alexander.request_blocker.blocking.storage.api;

import ru.alexander.request_blocker.blocking.ip.api.exceptions.ExecutionBlockException;

/**
 * Class name says it all.
 * Contains business logic of managing request counters.
 */
public interface CommonCounterStorageOperations {
    void validateIPCount(int executionId, String ip) throws ExecutionBlockException;

    void clearStorage();
}
