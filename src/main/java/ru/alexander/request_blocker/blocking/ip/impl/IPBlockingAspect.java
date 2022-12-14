package ru.alexander.request_blocker.blocking.ip.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import ru.alexander.request_blocker.blocking.ip.api.CurrentIPProvider;
import ru.alexander.request_blocker.blocking.ip.api.exceptions.ExecutionBlockException;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterStorageOperations;

/**
 * Aspect controls triggering of limitation logic
 */
@Aspect("pertarget(ru.alexander.request_blocker.blocking.ip.impl.IPBlockingAspect.checkForRequestsPerIP())")
@RequiredArgsConstructor
class IPBlockingAspect {
    private final int executionID;
    private final CommonCounterStorageOperations storageOperations;
    private final CurrentIPProvider ipProvider;

    @Pointcut("@annotation(ru.alexander.request_blocker.blocking.ip.api.LimitSameIP)")
    public void checkForRequestsPerIP() {
    }

    @Before("checkForRequestsPerIP()")
    public void verifyIPCount(JoinPoint joinPoint) throws ExecutionBlockException {
        val ip = ipProvider.getCurrentIPAddress();
        storageOperations.validateIPCount(executionID, ip);
    }
}
