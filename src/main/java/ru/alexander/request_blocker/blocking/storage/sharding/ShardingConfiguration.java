package ru.alexander.request_blocker.blocking.storage.sharding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterStorageOperations;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.api.locks.ShardStorageLock;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.IPTypesShardingStrategy;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.IPv4ShardingStrategy;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.IPv6ShardingStrategy;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.ShardingStrategy;

@Configuration
public class ShardingConfiguration {
    @Value("${block_ip.requests.limit:10}")
    private int requestLimit;

    @Bean
    public ShardingStrategy getIPShardingStrategy() {
        return new IPTypesShardingStrategy(
            new IPv4ShardingStrategy(),
            new IPv6ShardingStrategy()
        );
    }

    @Bean
    public ShardStorageLock shardStorageLock(ShardingStrategy shardingStrategy) {
        return new ShardStorageLock(shardingStrategy);
    }

    @Bean
    public CommonCounterStorageOperations commonCounterLogic(
        ShardStorageLock storageLock,
        CountersStorage storage
    ) {
        return new ShardThreadSafeCounterOperations(
            storageLock,
            storageLock,
            storage,
            requestLimit
        );
    }
}
