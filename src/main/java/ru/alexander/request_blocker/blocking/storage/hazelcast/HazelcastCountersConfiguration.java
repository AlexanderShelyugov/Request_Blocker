package ru.alexander.request_blocker.blocking.storage.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;

@Configuration
public class HazelcastCountersConfiguration {
    @Bean
    public HazelcastInstance getHazelcast(Config config) {
        return newHazelcastInstance(config);
    }

    @Bean
    public Config getHazelcastConfig() {
        Config config = new Config();
        config.getNetworkConfig().setPort(5900)
            .setPortAutoIncrement(false);
        return config;
    }
}
