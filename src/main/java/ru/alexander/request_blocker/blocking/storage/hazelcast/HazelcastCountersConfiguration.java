package ru.alexander.request_blocker.blocking.storage.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import ru.alexander.request_blocker.blocking.storage.api.CommonCounterStorageOperations;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.api.locks.ShardStorageLock;
import ru.alexander.request_blocker.blocking.storage.operations.ShardThreadSafeCounterOperations;
import ru.alexander.request_blocker.blocking.storage.sharding.IPTypesShardingStrategy;
import ru.alexander.request_blocker.blocking.storage.sharding.IPv4ShardingStrategy;
import ru.alexander.request_blocker.blocking.storage.sharding.IPv6ShardingStrategy;
import ru.alexander.request_blocker.blocking.storage.sharding.ShardingStrategy;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;

@Configuration
@Profile({"default", "storage-hazelcast"})
@Primary
public class HazelcastCountersConfiguration {

    @Value("${block_ip.requests.amount:10}")
    private int requestLimit;

    @Value("${storage.hazelcast.port:5900}")
    private int hazelcastPort;
    @Value("${storage.hazelcast.allow_auto_increment:false}")
    private boolean hazelcastAllowAutoIncrement;


    @Bean
    public CountersStorage getHazelcastCountersStorage(
        HazelcastInstance hazelcast,
        ShardingStrategy shardingStrategy
    ) {
        return new HazelcastCountersStorage(hazelcast, shardingStrategy);
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

    @Bean
    public HazelcastInstance getHazelcast(Config config) {
        return newHazelcastInstance(config);
    }

    @Bean
    public Config getHazelcastConfig() {
        Config config = new Config();
        config.getNetworkConfig()
            .setPort(hazelcastPort)
            .setPortAutoIncrement(hazelcastAllowAutoIncrement);
        return config;
    }

    @Bean
    public ShardingStrategy getIPShardingStrategy() {
        return new IPTypesShardingStrategy(
            new IPv4ShardingStrategy(),
            new IPv6ShardingStrategy()
        );
    }
}
