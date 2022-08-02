package ru.alexander.request_blocker.blocking.storage.impl;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.util.IpAddressUtils;
import ru.alexander.request_blocker.util.RandomUtils;

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
import static ru.alexander.request_blocker.util.IpAddressUtils.randomIPv4Address;
import static ru.alexander.request_blocker.util.IpAddressUtils.randomIPv6Address;
import static ru.alexander.request_blocker.util.RandomUtils.RANDOM;
import static ru.alexander.request_blocker.util.RandomUtils.randomString;

class SimpleCountersStorageTest {
    private CountersStorage storage;

    @BeforeEach
    void setUp() {
        storage = new SimpleCountersStorage();
    }

    @Test
    @DisplayName("Retrieval of new parameters works correctly")
    void getCounterOrZero() {
        val cases = new HashMap<String, String>() {{
            // ExecutionID <-> IP address
            put(randomString(), randomIPv4Address());
            put(randomString(), randomIPv6Address());
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
        val cases = new HashMap<String, String>() {{
            // ExecutionID <-> IP address
            put(randomString(), randomIPv4Address());
            put(randomString(), randomIPv6Address());
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
            generate(IpAddressUtils::randomIPAddress)
                .distinct()
                .limit(ipsCount)
                .collect(toSet());

        // Init cases.
        val cases = generate(RandomUtils::randomString)
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
        Map<String, Set<String>> cases,
        BiConsumer<String, String> consumer
    ) {
        cases.entrySet().stream()
            .flatMap(
                (entry) -> entry.getValue().stream()
                    .map((ip) -> new String[]{entry.getKey(), ip})
            )
            .forEach((entry) -> consumer.accept(entry[0], entry[1]));
    }
}