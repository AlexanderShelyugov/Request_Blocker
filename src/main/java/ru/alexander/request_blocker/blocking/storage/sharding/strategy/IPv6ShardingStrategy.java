package ru.alexander.request_blocker.blocking.storage.sharding.strategy;

import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Long.min;
import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.of;

/**
 * Sharding strategy for IPv6 addresses.
 * <p>
 * Current implementation works with only first two blocks.
 * <p>
 * Overall spectrum between 0000:0000 and ffff.ffff is divided by shards number.
 * <p>
 * Incoming IP address is located in some range - that will be our shard.
 * <p>
 * Note. We DO pay attention to compressed forms of IP Address.
 */
public class IPv6ShardingStrategy implements ShardingStrategy {
    private static final long IPV6_DEFAULT_SHARDS_COUNT = 1000;
    private static final long VALUES_PER_BLOCK = 65536L;
    // We take into account first two numbers of IP address
    private static final long MAX_ITEMS_COUNT = VALUES_PER_BLOCK * VALUES_PER_BLOCK;

    // Execution ID - Range name
    private static final String SHARD_NAME_FORMAT = "%d-%s";

    /**
     * Symbol, that divides addresses' blocks
     */
    private static final String SEPARATOR = ":";

    private final Map<Long, String> shardRanges;

    public IPv6ShardingStrategy() {
        this(IPV6_DEFAULT_SHARDS_COUNT);
    }

    public IPv6ShardingStrategy(long shardsCount) {
        shardsCount = min(shardsCount, Integer.MAX_VALUE);
        shardRanges = createIPv6ShardRanges((int) shardsCount);
    }

    @Override
    public String getShardName(int executionID, String ipv6) {
        // We take first two numbers of address,
        // and calculating their position on overall spectrum [0:0 - ffff:ffff].
        // After we know position, we look which region this position fits to.
        // When we've figured the range, we know the shard's name!

        val ipToken = ipToSpectrumPosition(ipv6);
        final String rangeName = shardRanges.entrySet().stream()
            .filter(range -> ipToken <= range.getKey())
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        return String.format(SHARD_NAME_FORMAT, executionID, rangeName);
    }

    private static Map<Long, String> createIPv6ShardRanges(int shardCount) {
        val result = new HashMap<Long, String>(shardCount);

        var itemsPerShard = MAX_ITEMS_COUNT / shardCount;
        var ipStep = 0L;
        var shardNum = 0L;
        do {
            ipStep = min(ipStep + itemsPerShard, MAX_ITEMS_COUNT);
            result.put(ipStep, "v6_" + shardNum);
            shardNum++;
        } while (MAX_ITEMS_COUNT != ipStep);

        return unmodifiableMap(result);
    }

    private static long ipToSpectrumPosition(String ip) {
        val firstSeparator = ip.indexOf(SEPARATOR);
        val secondSeparator = ip.indexOf(SEPARATOR, firstSeparator + 1);
        if (firstSeparator < 0L || secondSeparator < 0L) {
            throw new IllegalArgumentException("Failed to extract first two blocks from IP");
        }
        if (secondSeparator <= firstSeparator) {
            throw new IllegalArgumentException("IPv6 separators detection fault. Second separator appers to be not after the first one!");
        }
        return blockToLong(ip.substring(0, firstSeparator)) * VALUES_PER_BLOCK
            + blockToLong(ip.substring(firstSeparator + 1, secondSeparator));
    }

    private static long blockToLong(String block) {
        return of(block)
            .map(String::trim)
            .map(b -> Long.parseLong(b, 16))
            .orElse(0L); // We don't throw here, because IPv6 can be in compressed format.
    }
}
