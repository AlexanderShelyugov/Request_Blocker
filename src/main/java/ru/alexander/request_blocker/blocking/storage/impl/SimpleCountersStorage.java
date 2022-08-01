package ru.alexander.request_blocker.blocking.storage.impl;

import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;

import java.util.HashMap;
import java.util.Map;

public class SimpleCountersStorage implements CountersStorage {

    private final Map<String, Integer> storageMap;

    public SimpleCountersStorage(int targetsCount) {
        storageMap = new HashMap<>(targetsCount);
    }

    @Override
    public int getCounterOrZero(String executionID) {
        return storageMap.computeIfAbsent(executionID, (key) -> 0);
    }

    @Override
    public void setCounter(String executionID, int newValue) {
        storageMap.put(executionID, newValue);
    }

    @Override
    public void removeAllCounters() {
        storageMap.clear();
    }
}
