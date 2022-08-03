package ru.alexander.request_blocker.blocking.storage.cleanup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterStorageOperations;

@Configuration
@EnableScheduling
public class CountersCleanupConfiguration {
    @Bean
    public CounterStorageCleanupTask counterStorageCleanupTask(CommonCounterStorageOperations counterLogic) {
        return new CounterStorageCleanupTask(counterLogic);
    }
}
