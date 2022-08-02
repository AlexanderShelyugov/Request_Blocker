package ru.alexander.request_blocker.blocking.storage.api;

import ru.alexander.request_blocker.blocking.ip.api.exceptions.ExecutionBlockException;

public interface CommonCounterLogic {
    void validateIPCount(int executionId, String ip) throws ExecutionBlockException;

    void clearStorage();
}
