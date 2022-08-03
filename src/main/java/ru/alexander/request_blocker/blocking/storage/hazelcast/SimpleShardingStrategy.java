package ru.alexander.request_blocker.blocking.storage.hazelcast;

import lombok.val;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.min;
import static java.util.Collections.unmodifiableMap;

@Service
class SimpleShardingStrategy implements ShardingStrategy {
    private static final String SHARD_NAME_FORMAT = "%d-%s";
    private static final int IPV4_DEFAULT_SHARDS_COUNT = 100;
    private static final int IPV6_DEFAULT_SHARDS_COUNT = 1000;

    private final Map<String, Integer> ipv4ShardRanges;
    private final Map<String, Long> ipv6ShardRanges;

    public SimpleShardingStrategy() {
        this(IPV4_DEFAULT_SHARDS_COUNT, IPV6_DEFAULT_SHARDS_COUNT);
    }

    public SimpleShardingStrategy(int ipv4ShardsCount, int ipv6ShardsCount) {
        if (ipv4ShardsCount <= 0) throw new IllegalArgumentException("Can't have non-positive shard count");
        if (ipv6ShardsCount <= 0) throw new IllegalArgumentException("Can't have non-positive shard count");
        ipv4ShardRanges = createIPv4ShardRanges(ipv4ShardsCount);
        ipv6ShardRanges = createIPv6ShardRanges(ipv6ShardsCount);
    }

    @Override
    public String getShardName(int executionID, String ip) {
        val ipShard = getShardNameFor(ip);
        return String.format(SHARD_NAME_FORMAT, executionID, ipShard);
    }

    private String getShardNameFor(String ip) {
        var shardName = "default";
        val validator = InetAddressValidator.getInstance();
        if (validator.isValidInet4Address(ip)) {
            shardName = getShardNameIpV4(ip);
        } else if (validator.isValidInet6Address(ip)) {
            shardName = getShardNameIpV6(ip);
        }
        return shardName;
    }

    private String getShardNameIpV4(String ipv4) {
        val ipToken = Optional.of(ipv4.split("\\."))
            .map(ip -> new Integer[]{Integer.parseInt(ip[0]), Integer.parseInt(ip[1])})
            .map(ipParts -> ipParts[0] * ipParts[1])
            .orElseThrow(IllegalStateException::new);
        return ipv4ShardRanges.entrySet().stream()
            .filter(range -> ipToken <= range.getValue())
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    private String getShardNameIpV6(String ipv6) {
        val ipToken = Optional.of(ipv6.split(":"))
            .map(ip -> new Long[]{Long.parseLong(ip[0]), Long.parseLong(ip[1])})
            .map(ipParts -> ipParts[0] * ipParts[1])
            .orElseThrow(IllegalStateException::new);
        return ipv6ShardRanges.entrySet().stream()
            .filter(range -> ipToken <= range.getValue())
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    }

    private Map<String, Integer> createIPv4ShardRanges(int shardCount) {
        val result = new HashMap<String, Integer>(shardCount);
        // We take into account first two numbers of IP address
        val ceiling = 256 * 256;
        var itemsPerShard = ceiling / shardCount;
        var ipStep = 0;
        var shardNum = 0;
        do {
            ipStep = min(ipStep + itemsPerShard, ceiling);
            result.put("v4_" + shardNum, ipStep);
            shardNum++;
        } while (ceiling != ipStep);

        return unmodifiableMap(result);
    }

    private static Map<String, Long> createIPv6ShardRanges(int shardCount) {
        val result = new HashMap<String, Long>(shardCount);
        // We take into account first two numbers of IP address
        val ceiling = 65536L * 65536L;
        var itemsPerShard = ceiling / shardCount;
        var ipStep = 0L;
        var shardNum = 0L;
        do {
            ipStep = min(ipStep + itemsPerShard, ceiling);
            result.put("v6_" + shardNum, ipStep);
            shardNum++;
        } while (ceiling != ipStep);

        return unmodifiableMap(result);
    }

}
