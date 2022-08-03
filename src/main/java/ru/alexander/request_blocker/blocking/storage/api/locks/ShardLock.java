package ru.alexander.request_blocker.blocking.storage.api.locks;

/**
 * This lock manages access to a shard, targeted by executionID and IP address
 */
public interface ShardLock {
    /**
     * Locks shard targeted by executionID and IP
     */
    void lock(int executionID, String ip);

    /**
     * Unlocks shard targeted by executionID and IP
     */
    void unlock(int executionID, String ip);
}
