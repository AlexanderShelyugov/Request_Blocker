package ru.alexander.request_blocker.blocking.storage.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterLogic;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;

@Configuration
public class CountersStorageConfiguration {
    @Value("${block_ip.requests.amount:10}")
    private int requestLimit;

    @Bean
    public CountersStorage createCountersStorage() {
        return new SimpleCountersStorage();
    }

    @Bean
    public CommonCounterLogic commonCounterLogic(CountersStorage storage) {
        return new ThreadSafeCommonCounterLogic(storage, requestLimit);
    }
}
