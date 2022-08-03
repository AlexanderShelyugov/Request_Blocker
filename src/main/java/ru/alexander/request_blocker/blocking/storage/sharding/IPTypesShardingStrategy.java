package ru.alexander.request_blocker.blocking.storage.sharding;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.InetAddressValidator;

@RequiredArgsConstructor
public class IPTypesShardingStrategy implements ShardingStrategy {
    private static final InetAddressValidator IP_VALIDATOR = InetAddressValidator.getInstance();
    private final ShardingStrategy ipv4ShardingStrategy;
    private final ShardingStrategy ipv6ShardingStrategy;

    @Override
    public String getShardName(int executionID, String ip) {
        final String shardName;
        if (IP_VALIDATOR.isValidInet4Address(ip)) {
            shardName = ipv4ShardingStrategy.getShardName(executionID, ip);
        } else if (IP_VALIDATOR.isValidInet6Address(ip)) {
            shardName = ipv6ShardingStrategy.getShardName(executionID, ip);
        } else {
            shardName = "default";
        }
        return shardName;
    }

}
