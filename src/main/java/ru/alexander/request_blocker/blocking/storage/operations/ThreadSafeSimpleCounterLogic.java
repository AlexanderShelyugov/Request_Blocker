package ru.alexander.request_blocker.blocking.storage.operations;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.alexander.request_blocker.blocking.ip.api.exceptions.ExecutionBlockException;
import ru.alexander.request_blocker.blocking.ip.api.exceptions.TooManyRequestsByIPException;
import ru.alexander.request_blocker.blocking.ip.api.exceptions.UnableToGetIPException;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterLogic;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

@RequiredArgsConstructor
@Slf4j
public class ThreadSafeSimpleCounterLogic implements CommonCounterLogic {
    private final CountersStorage storage;
    private final int requestsLimit;

    @Override
    @Synchronized
    public void validateIPCount(int executionID, String ip) throws ExecutionBlockException {
        if (ofNullable(ip).filter(not(String::isBlank)).isEmpty()) {
            throw new UnableToGetIPException("Unable to retrieve IP address. Exit execution.");
        }
        val counter = storage.getCounterOrZero(executionID, ip);
        log.debug("IP: {}, count: {}", ip, counter);
        if (requestsLimit <= counter) {
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
