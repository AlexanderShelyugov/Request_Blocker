package ru.alexander.request_blocker.blocking.storage.sharding;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterStorageOperations;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.api.locks.ShardStorageLock;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.IPTypesShardingStrategy;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.IPv4ConsistentHashingShardingStrategy;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.IPv6ConsistentHashingShardingStrategy;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.ShardingStrategy;

@Configuration
public class ShardingConfiguration {
    @Value("${block_ip.requests.limit:10}")
    private int requestLimit;

    @Value("${block_ip.shards.ipv4:0}")
    private int ipv4ShardsCount;

    @Value("${block_ip.shards.ipv6:0}")
    private int ipv6ShardsCount;

    @Bean
    public ShardingStrategy getIPShardingStrategy() {
        val ipv4Strategy = new IPv4ConsistentHashingShardingStrategy(ipv4ShardsCount);
        val ipv6Strategy = new IPv6ConsistentHashingShardingStrategy(ipv6ShardsCount);

        return IPTypesShardingStrategy.builder()
            .ipv4ShardingStrategy(ipv4Strategy)
            .ipv6ShardingStrategy(ipv6Strategy)
            .build();
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
