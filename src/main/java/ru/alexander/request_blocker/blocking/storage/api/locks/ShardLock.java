package ru.alexander.request_blocker.blocking.storage.api.locks;

/**
 * This lock managess access according to current credentials - executionID and IP address
 */
public interface ShardLock {
    void lock(int executionID, String ip);

    void unlock(int executionID, String ip);
}
