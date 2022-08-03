package ru.alexander.request_blocker.blocking.storage.api.locks;

/**
 * This lock controls overall access to the storage.
 * <p>
 * If the storage is locked - all requests for shards are paused until unlocked.
 */
public interface StorageLock {
    /**
     * Locks storage
     */
    void lock();

    /**
     * Unlocks storage
     */
    void unlock();
}
