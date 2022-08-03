package ru.alexander.request_blocker.blocking.storage.simple;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.sharding.ShardingStrategy;

@Configuration
@Profile({"default", "storage-simple"})
public class SimpleStorageConfiguration {
    @Bean
    public CountersStorage createCountersStorage(ShardingStrategy shardingStrategy) {
        return new SimpleCountersStorage(shardingStrategy);
    }
}
