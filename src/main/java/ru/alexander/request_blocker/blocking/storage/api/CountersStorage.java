package ru.alexander.request_blocker.blocking.storage.api;

/**
 * Storage of counters.
 */
public interface CountersStorage {
    /**
     * Returns current counter for a given execution.
     *
     * @param executionID execution ID
     * @return counter value
     */
    int getCounterOrZero(int executionID, String ip);

    /**
     * Sets new counter value for a given execution.
     *
     * @param executionID execution ID
     * @param newValue    new counter value
     */
    void setCounter(int executionID, String ip, int newValue);

    /**
     * Removes all counters from storage.
     */
    void removeAllCounters();
}
