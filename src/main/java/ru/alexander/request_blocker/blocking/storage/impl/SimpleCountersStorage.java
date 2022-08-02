package ru.alexander.request_blocker.blocking.storage.impl;

import lombok.val;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;

import java.util.HashMap;
import java.util.Map;

class SimpleCountersStorage implements CountersStorage {
    private Map<String, Map<String, Integer>> storageMap;

    public SimpleCountersStorage() {
        flashStorage();
    }

    @Override
    public int getCounterOrZero(String executionID, String ip) {
        val ipCounters = getCountersForExecution(executionID);
        return ipCounters.computeIfAbsent(ip, key -> 0);
    }

    @Override
    public void setCounter(String executionID, String ip, int newValue) {
        val ipCounters = getCountersForExecution(executionID);
        ipCounters.put(ip, newValue);
    }

    @Override
    public void removeAllCounters() {
        flashStorage();
    }

    private Map<String, Integer> getCountersForExecution(String executionID) {
        return storageMap.computeIfAbsent(executionID, key -> new HashMap<>());
    }

    private void flashStorage() {
        storageMap = new HashMap<>();
    }
}
