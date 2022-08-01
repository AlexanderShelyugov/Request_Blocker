package ru.alexander.request_blocker.blocking.storage.api;

/**
 * API for managing counters.
 */
public interface CountersStorage {
    /**
     * Returns current counter for a given execution.
     *
     * @param executionID execution ID
     * @return counter value
     */
    int getCounterOrZero(String executionID);

    /**
     * Sets new counter value for a given execution.
     *
     * @param executionID execution ID
     * @param newValue    new counter value
     */
    void setCounter(String executionID, int newValue);

    /**
     * Removes all counters from storage.
     */
    void removeAllCounters();
}
