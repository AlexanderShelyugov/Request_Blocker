package ru.alexander.request_blocker.blocking.ip.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.alexander.request_blocker.blocking.ip.api.CurrentIPProvider;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterLogic;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class IPBlockingConfiguration {
    private static final AtomicInteger EXECUTIONS = new AtomicInteger();

    @Bean
    @Scope("prototype")
    public IPBlockingAspect ipBlockingAspect(
        CommonCounterLogic storageLogic,
        CurrentIPProvider ipProvider
    ) {
        return new IPBlockingAspect(
            EXECUTIONS.incrementAndGet(),
            storageLogic,
            ipProvider
        );
    }
}
