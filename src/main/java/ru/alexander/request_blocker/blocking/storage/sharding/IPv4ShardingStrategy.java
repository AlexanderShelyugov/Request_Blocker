package ru.alexander.request_blocker.blocking.storage.sharding;

import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.min;
import static java.util.Collections.unmodifiableMap;

public class IPv4ShardingStrategy implements ShardingStrategy {
    private static final int IPV4_DEFAULT_SHARDS_COUNT = 100;
    private static final int VALUES_PER_BLOCK = 256;
    // We take into account only first two numbers of IP address
    private static final int MAX_ITEMS_COUNT = VALUES_PER_BLOCK * VALUES_PER_BLOCK;
    private static final String SHARD_NAME_FORMAT = "%d-%s";
    private static final String SEPARATOR_REGEXP = "\\.";

    private final Map<String, Integer> shardRanges;

    public IPv4ShardingStrategy() {
        this(IPV4_DEFAULT_SHARDS_COUNT);
    }

    public IPv4ShardingStrategy(int shardsCount) {
        if (shardsCount <= 0) throw new IllegalArgumentException("Can't have non-positive shard count");
        shardRanges = createIPv4ShardRanges(shardsCount);
    }

    @Override
    public String getShardName(int executionID, String ipv4) {
        // We take first two numbers of address,
        // and calculating their position on overall spectrum [0.0 - 255.255].
        // After we know position, we look which region this position fits to.
        // When we've figured the range, we know the shard's name!
        val ipToken = Optional.of(ipv4.split(SEPARATOR_REGEXP))
            .map(ip -> new Integer[]{Integer.parseInt(ip[0]), Integer.parseInt(ip[1])})
            .map(ipParts -> ipParts[0] * VALUES_PER_BLOCK + ipParts[1])
            .orElseThrow(IllegalStateException::new);
        val ipRangeName = shardRanges.entrySet().stream()
            .filter(range -> ipToken <= range.getValue())
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(IllegalStateException::new);
        return String.format(SHARD_NAME_FORMAT, executionID, ipRangeName);
    }

    private Map<String, Integer> createIPv4ShardRanges(int shardCount) {
        val result = new HashMap<String, Integer>(shardCount);

        var itemsPerShard = MAX_ITEMS_COUNT / shardCount;
        var ipStep = 0;
        var shardNum = 0;
        do {
            ipStep = min(ipStep + itemsPerShard, MAX_ITEMS_COUNT);
            result.put("v4_" + shardNum, ipStep);
            shardNum++;
        } while (MAX_ITEMS_COUNT != ipStep);

        return unmodifiableMap(result);
    }
}
