package ru.alexander.request_blocker.blocking.storage.impl;

import lombok.val;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;

import java.util.HashMap;
import java.util.Map;

class SimpleCountersStorage implements CountersStorage {
    private Map<Integer, Map<String, Integer>> storageMap;

    public SimpleCountersStorage() {
        flashStorage();
    }

    @Override
    public int getCounterOrZero(int executionID, String ip) {
        val ipCounters = getCountersForExecution(executionID);
        return ipCounters.computeIfAbsent(ip, key -> 0);
    }

    @Override
    public void setCounter(int executionID, String ip, int newValue) {
        val ipCounters = getCountersForExecution(executionID);
        ipCounters.put(ip, newValue);
    }

    @Override
    public void removeAllCounters() {
        flashStorage();
    }

    private Map<String, Integer> getCountersForExecution(int executionID) {
        return storageMap.computeIfAbsent(executionID, key -> new HashMap<>());
    }

    private void flashStorage() {
        storageMap = new HashMap<>();
    }
}
