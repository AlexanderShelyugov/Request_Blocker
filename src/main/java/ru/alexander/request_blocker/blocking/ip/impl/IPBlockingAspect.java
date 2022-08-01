package ru.alexander.request_blocker.blocking.ip.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.api.TooManyRequestsByIPException;

import java.util.UUID;

@Aspect("pertarget(ru.alexander.request_blocker.blocking.ip.impl.IPBlockingAspect.checkForRequestsPerIP())")
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class IPBlockingAspect {
    private static final int MAX_COUNTER = 3;

    private final String executionID = UUID.randomUUID().toString();

    private final CountersStorage storage;

    @Pointcut("@annotation(ru.alexander.request_blocker.blocking.ip.api.IPBlocks)")
    public void checkForRequestsPerIP() {
    }

    @Around("checkForRequestsPerIP()")
    public Object verifyIPCount(ProceedingJoinPoint joinPoint) throws Throwable {
        synchronized (storage) {
            val counter = storage.getCounterOrZero(executionID);
            if (MAX_COUNTER < counter) {
                throw new TooManyRequestsByIPException();
            }
            storage.setCounter(executionID, counter + 1);
        }
        return joinPoint.proceed();
    }
}
