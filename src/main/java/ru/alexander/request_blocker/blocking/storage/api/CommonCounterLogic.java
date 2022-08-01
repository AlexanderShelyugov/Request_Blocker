package ru.alexander.request_blocker.blocking.storage.api;

public interface CommonCounterLogic {
    void validateIPCount(String executionId, String ip) throws ExecutionBlockException;

    void clearStorage();
}
