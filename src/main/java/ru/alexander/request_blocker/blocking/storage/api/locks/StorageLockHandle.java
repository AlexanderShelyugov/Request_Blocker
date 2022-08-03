package ru.alexander.request_blocker.blocking.storage.api.locks;

public interface StorageLockHandle {
    void lock();

    void release();
}
