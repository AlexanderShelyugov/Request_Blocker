package ru.alexander.request_blocker.blocking.storage.sharding;

import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Long.min;
import static java.util.Collections.unmodifiableMap;

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
    private static final String SEPARATOR_REGEXP = ":";

    private final Map<String, Long> shardRanges;

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

        val ipToken = Optional.of(ipv6.split(SEPARATOR_REGEXP))
            .map(ip -> new Long[]{Long.parseLong(ip[0]), Long.parseLong(ip[1])})
            .map(ipParts -> ipParts[0] * VALUES_PER_BLOCK + ipParts[1])
            .orElseThrow(IllegalStateException::new);
        val rangeName = shardRanges.entrySet().stream()
            .filter(range -> ipToken <= range.getValue())
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        return String.format(SHARD_NAME_FORMAT, executionID, rangeName);
    }

    private static Map<String, Long> createIPv6ShardRanges(int shardCount) {
        val result = new HashMap<String, Long>(shardCount);

        var itemsPerShard = MAX_ITEMS_COUNT / shardCount;
        var ipStep = 0L;
        var shardNum = 0L;
        do {
            ipStep = min(ipStep + itemsPerShard, MAX_ITEMS_COUNT);
            result.put("v6_" + shardNum, ipStep);
            shardNum++;
        } while (MAX_ITEMS_COUNT != ipStep);

        return unmodifiableMap(result);
    }

}
