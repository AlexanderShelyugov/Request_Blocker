package ru.alexander.request_blocker.blocking.storage.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterLogic;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;

@Configuration
class CountersStorageConfiguration {
    @Bean
    public CountersStorage createCountersStorage() {
        return new SimpleCountersStorage(100);
    }

    @Bean
    public CommonCounterLogic commonCounterLogic(CountersStorage storage) {
        return new ThreadSafeCommonCounterLogic(storage);
    }
}
