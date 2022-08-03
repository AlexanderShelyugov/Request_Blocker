package ru.alexander.request_blocker.blocking.storage.simple;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.ShardingStrategy;
import ru.alexander.request_blocker.util.IpAddressHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Stream.generate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.alexander.request_blocker.util.IpAddressHelper.randomIPv4Address;
import static ru.alexander.request_blocker.util.IpAddressHelper.randomIPv6Address;
import static ru.alexander.request_blocker.util.RandomHelper.RANDOM;

class SimpleCountersStorageTest {
    private CountersStorage storage;

    @BeforeEach
    void setUp() {
        storage = new SimpleCountersStorage(new SimpleShardingStrategy());
    }

    @Test
    @DisplayName("Retrieval of new parameters works correctly")
    void getCounterOrZero() {
        val cases = new HashMap<Integer, String>() {{
            // ExecutionID <-> IP address
            put(RANDOM.nextInt(), randomIPv4Address());
            put(RANDOM.nextInt(), randomIPv6Address());
        }};

        // For all cases initial counter retrieval returns zero.
        cases.forEach(
            (executionID, ipAddress) ->
                assertEquals(0, storage.getCounterOrZero(executionID, ipAddress))
        );
    }

    @Test
    @DisplayName("Setting counters works")
    void setCounter() {
        val cases = new HashMap<Integer, String>() {{
            // ExecutionID <-> IP address
            put(RANDOM.nextInt(), randomIPv4Address());
            put(RANDOM.nextInt(), randomIPv6Address());
        }};

        // For all cases after setting counter we receive the same value.
        cases.forEach((executionID, ipAddress) -> {
            assertEquals(0, storage.getCounterOrZero(executionID, ipAddress));
            val someCount = RANDOM.nextInt();
            storage.setCounter(executionID, ipAddress, someCount);
            assertEquals(someCount, storage.getCounterOrZero(executionID, ipAddress));
        });
    }

    @Test
    @DisplayName("Cleaning storage works")
    void removeAllCounters() {
        val executionsCount = RANDOM.nextInt(10);
        // IPs per execution
        val ipsCount = RANDOM.nextInt(10);
        // Easy way to generate N random distinct IP addresses
        final Function<Integer, Set<String>> ipsGenerator = (count) ->
            generate(IpAddressHelper::randomIPAddress)
                .distinct()
                .limit(ipsCount)
                .collect(toSet());

        // Init cases.
        val cases = generate(RANDOM::nextInt)
            .limit(executionsCount)
            .collect(toUnmodifiableMap(
                identity(),
                (executionID) -> ipsGenerator.apply(ipsCount))
            );

        // Init storage
        iterateOverStorage(cases, (executionID, ip) -> {
            // Just to be sure - we had empty count here
            assertEquals(0, storage.getCounterOrZero(executionID, ip));
            val count = RANDOM.nextInt();
            storage.setCounter(executionID, ip, count);
            // Ok, we have some counters here
            assertEquals(count, storage.getCounterOrZero(executionID, ip));
        });

        // Run cleanup
        storage.removeAllCounters();

        // Verify that storage is empty
        iterateOverStorage(cases, (executionID, ip) -> {
            assertEquals(0, storage.getCounterOrZero(executionID, ip));
        });
    }

    private void iterateOverStorage(
        Map<Integer, Set<String>> cases,
        BiConsumer<Integer, String> consumer
    ) {
        cases.entrySet().stream()
            .flatMap(
                (entry) -> entry.getValue().stream()
                    .map((ip) -> new Object[]{entry.getKey(), ip})
            )
            .forEach((entry) -> consumer.accept((Integer) entry[0], (String) entry[1]));
    }

    private static class SimpleShardingStrategy implements ShardingStrategy {
        @Override
        public String getShardName(int executionID, String ip) {
            return "shard-name";
        }
    }
}