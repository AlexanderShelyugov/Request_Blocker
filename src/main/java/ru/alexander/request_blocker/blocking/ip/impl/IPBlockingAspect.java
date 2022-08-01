package ru.alexander.request_blocker.blocking.ip.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterLogic;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.api.TooManyRequestsByIPException;

import java.util.UUID;

@Aspect("pertarget(ru.alexander.request_blocker.blocking.ip.impl.IPBlockingAspect.checkForRequestsPerIP())")
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class IPBlockingAspect {
    private final String executionID = UUID.randomUUID().toString();

    private final CommonCounterLogic storageLogic;

    @Pointcut("@annotation(ru.alexander.request_blocker.blocking.ip.api.IPBlocks)")
    public void checkForRequestsPerIP() {
    }

    @Around("checkForRequestsPerIP()")
    public Object verifyIPCount(ProceedingJoinPoint joinPoint) throws Throwable {
        storageLogic.validateIPCount(executionID, "");
        return joinPoint.proceed();
    }
}