package ru.alexander.request_blocker.blocking.storage.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.ShardingStrategy;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;

@Configuration
@Profile("storage-hazelcast")
@Primary
public class HazelcastCountersConfiguration {
    @Value("${storage.hazelcast.port:5900}")
    private int hazelcastPort;
    @Value("${storage.hazelcast.allow_auto_increment:false}")
    private boolean hazelcastAllowAutoIncrement;

    @Bean
    public CountersStorage getHazelcastCountersStorage(
        ShardingStrategy shardingStrategy,
        HazelcastInstance hazelcast
    ) {
        return new HazelcastCountersStorage(shardingStrategy, hazelcast);
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
}
