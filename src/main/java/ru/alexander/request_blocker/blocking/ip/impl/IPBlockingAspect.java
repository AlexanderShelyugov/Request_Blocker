package ru.alexander.request_blocker.blocking.ip.impl;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.alexander.request_blocker.blocking.ip.api.CurrentIPProvider;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterLogic;

import java.util.UUID;

@Aspect("pertarget(ru.alexander.request_blocker.blocking.ip.impl.IPBlockingAspect.checkForRequestsPerIP())")
@Component
@Scope("prototype")
@RequiredArgsConstructor
class IPBlockingAspect {
    private final String executionID = UUID.randomUUID().toString();
    private final CommonCounterLogic storageLogic;
    private final CurrentIPProvider ipProvider;

    @Pointcut("@annotation(ru.alexander.request_blocker.blocking.ip.api.LimitSameIP)")
    public void checkForRequestsPerIP() {
    }

    @Around("checkForRequestsPerIP()")
    public Object verifyIPCount(ProceedingJoinPoint joinPoint) throws Throwable {
        storageLogic.validateIPCount(executionID, ipProvider.getCurrentIPAddress());
        return joinPoint.proceed();
    }
}
