package ru.alexander.request_blocker.blocking.storage.sharding.strategy;

import lombok.val;

import java.util.stream.LongStream;

import static java.lang.Long.min;
import static java.util.Optional.of;
import static java.util.stream.LongStream.iterate;

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
public class IPv6ConsistentHashingShardingStrategy implements ShardingStrategy {
    private static final long IPV6_DEFAULT_SHARDS_COUNT = 1000;
    private static final long VALUES_PER_BLOCK = 65536L;
    // We take into account first two numbers of IP address
    private static final long MAX_ITEMS_COUNT = VALUES_PER_BLOCK * VALUES_PER_BLOCK;

    // Execution ID - Range name
    private static final String SHARD_NAME_FORMAT = "%d-v6-%d";

    /**
     * Symbol, that divides addresses' blocks
     */
    private static final String SEPARATOR = ":";

    private final long[] shardRanges;

    public IPv6ConsistentHashingShardingStrategy() {
        this(IPV6_DEFAULT_SHARDS_COUNT);
    }

    public IPv6ConsistentHashingShardingStrategy(long shardsCount) {
        shardsCount = min(shardsCount, Integer.MAX_VALUE);
        if (shardsCount < 0) throw new IllegalArgumentException("Can't have a negative shard count");
        if (shardsCount == 0) shardsCount = IPV6_DEFAULT_SHARDS_COUNT;
        shardRanges = createIPv6ShardRanges((int) shardsCount);
    }

    @Override
    public String getShardName(int executionID, String ipv6) {
        // We take first two numbers of address,
        // and calculating their position on overall spectrum [0:0 - ffff:ffff].
        // After we know position, we look which region this position fits to.
        // When we've figured the range, we know the shard's name!

        val ipToken = ipToSpectrumPosition(ipv6);
        var left = 0;
        var right = shardRanges.length - 1;
        long currentRange;
        var rangeNumber = 0;
        do {
            rangeNumber = (left + right) / 2;
            currentRange = shardRanges[rangeNumber];

            if (
                // We match current range, but previous range is less than our token
                (rangeNumber == 0 || shardRanges[rangeNumber - 1] < ipToken) && ipToken <= currentRange || left == right) {
                break;
            } else if (currentRange < ipToken) {
                left = rangeNumber + 1;
            } else { // ipToken <= currentRange
                right = rangeNumber;
            }
        } while (left <= right);
        return String.format(SHARD_NAME_FORMAT, executionID, rangeNumber);
    }

    private long[] createIPv6ShardRanges(int shardCount) {
        var itemsPerShard = MAX_ITEMS_COUNT / shardCount;
        val shards = iterate(itemsPerShard, prevRange -> prevRange + itemsPerShard)
                         .limit(shardCount)
                         .toArray();
        // Since integers don't always evenly divide
        // We need to compensate the error
        shards[shards.length - 1] = MAX_ITEMS_COUNT;
        return shards;
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
        return blockToLong(ip.substring(0, firstSeparator)) * VALUES_PER_BLOCK + blockToLong(ip.substring(firstSeparator + 1, secondSeparator));
    }

    private static long blockToLong(String block) {
        return of(block).map(String::trim).map(b -> Long.parseLong(b, 16)).orElse(0L); // We don't throw here, because IPv6 can be in compressed format.
    }
}
