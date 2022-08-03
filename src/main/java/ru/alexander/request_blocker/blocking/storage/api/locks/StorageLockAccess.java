package ru.alexander.request_blocker.blocking.storage.api.locks;

public interface StorageLockAccess {
    boolean canAccessStorage();

    void lockAccess(int executionID, String ip);

    void unlockAccess(int executionID, String ip);
}
