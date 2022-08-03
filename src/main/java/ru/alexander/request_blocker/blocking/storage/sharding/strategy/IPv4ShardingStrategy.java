package ru.alexander.request_blocker.blocking.storage.sharding.strategy;

import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.min;
import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.of;
import static java.util.function.Predicate.not;

/**
 * Sharding strategy for IPv4 addresses.
 * <p>
 * Current implementation works with only first two blocks.
 * <p>
 * Overall spectrum between 0.0 and 255.255 is divided by shards number.
 * <p>
 * Incoming IP address is located in some range - that will be our shard.
 */
public class IPv4ShardingStrategy implements ShardingStrategy {
    private static final int IPV4_DEFAULT_SHARDS_COUNT = 100;
    private static final int VALUES_PER_BLOCK = 256;
    // We take into account only first two blocks of IP address
    private static final int MAX_ITEMS_COUNT = VALUES_PER_BLOCK * VALUES_PER_BLOCK;
    private static final String SHARD_NAME_FORMAT = "%d-%s";
    private static final String SEPARATOR = ".";

    private final Map<Integer, String> shardRanges;

    public IPv4ShardingStrategy() {
        this(IPV4_DEFAULT_SHARDS_COUNT);
    }

    public IPv4ShardingStrategy(int shardsCount) {
        if (shardsCount < 0) throw new IllegalArgumentException("Can't have a negative shard count");
        if (shardsCount == 0) shardsCount = IPV4_DEFAULT_SHARDS_COUNT;
        shardRanges = createIPv4ShardRanges(shardsCount);
    }

    @Override
    public String getShardName(int executionID, String ipv4) {
        // We take first two numbers of address,
        // and calculating their position on overall spectrum [0.0 - 255.255].
        // After we know position, we look which range this position fits to.
        // When we've figured the range, we know the shard's name.
        val ipToken = ipToSpectrumPosition(ipv4);
        final String ipRangeName = shardRanges.entrySet().stream()
            .filter(range -> ipToken <= range.getKey())
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        return String.format(SHARD_NAME_FORMAT, executionID, ipRangeName);
    }

    private Map<Integer, String> createIPv4ShardRanges(int shardCount) {
        val result = new HashMap<Integer, String>(shardCount);

        var itemsPerShard = MAX_ITEMS_COUNT / shardCount;
        var ipStep = 0;
        var shardNum = 0;
        do {
            ipStep = min(ipStep + itemsPerShard, MAX_ITEMS_COUNT);
            result.put(ipStep, "v4_" + shardNum);
            shardNum++;
        } while (MAX_ITEMS_COUNT != ipStep);

        return unmodifiableMap(result);
    }

    private static int ipToSpectrumPosition(String ip) {
        val firstSeparator = ip.indexOf(SEPARATOR);
        val secondSeparator = ip.indexOf(SEPARATOR, firstSeparator + 1);
        if (firstSeparator < 0 || secondSeparator < 0) {
            throw new IllegalArgumentException("Failed to extract first two blocks from IP");
        }
        if (secondSeparator <= firstSeparator) {
            throw new IllegalArgumentException("IPv4 separators detection fault. Second separator appers to be not after the first one!");
        }
        return blockToInt(ip.substring(0, firstSeparator)) * VALUES_PER_BLOCK
            + blockToInt(ip.substring(firstSeparator + 1, secondSeparator));
    }

    private static int blockToInt(String block) {
        return of(block)
            .map(String::trim)
            .filter(not(String::isEmpty))
            .map(Integer::parseInt)
            .orElseThrow(() -> new IllegalArgumentException(
                "Failed to parse IP block. It appears to have separators close to each other."));
    }
}
