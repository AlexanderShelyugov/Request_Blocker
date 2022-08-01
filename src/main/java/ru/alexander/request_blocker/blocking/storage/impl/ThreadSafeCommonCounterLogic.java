package ru.alexander.request_blocker.blocking.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.val;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterLogic;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.api.exceptions.TooManyRequestsByIPException;

@RequiredArgsConstructor
class ThreadSafeCommonCounterLogic implements CommonCounterLogic {
    private static final int MAX_COUNTER = 3;
    private final CountersStorage storage;

    @Override
    @Synchronized
    public void validateIPCount(String executionID, String ip) {
        val counter = storage.getCounterOrZero(executionID, ip);
        if (MAX_COUNTER <= counter) {
            throw new TooManyRequestsByIPException();
        }
        storage.setCounter(executionID, ip, counter + 1);
    }

    @Override
    @Synchronized
    public void clearStorage() {
        storage.removeAllCounters();
    }
}
