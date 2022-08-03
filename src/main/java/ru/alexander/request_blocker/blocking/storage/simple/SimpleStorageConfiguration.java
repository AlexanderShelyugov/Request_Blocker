package ru.alexander.request_blocker.blocking.storage.simple;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterLogic;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.operations.ThreadSafeSimpleCounterLogic;

@Configuration
@Profile("storage-simple")
public class SimpleStorageConfiguration {
    @Value("${block_ip.requests.amount:10}")
    private int requestLimit;

    @Bean
    public CountersStorage createCountersStorage() {
        return new SimpleCountersStorage();
    }

    @Bean
    public CommonCounterLogic commonCounterLogic(CountersStorage storage) {
        return new ThreadSafeSimpleCounterLogic(storage, requestLimit);
    }
}
