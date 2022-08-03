package ru.alexander.request_blocker.blocking.storage.hazelcast;

import lombok.val;
import org.springframework.stereotype.Service;

@Service
class SimpleShardingStrategy implements ShardingStrategy {
    private static final String SHARD_NAME_FORMAT = "%d-%s";

    @Override
    public String getShardName(int executionID, String ip) {
        val ipShard = getShardNameFor(ip);
        return String.format(SHARD_NAME_FORMAT, executionID, ipShard);
    }

    private static String getShardNameFor(String ip) {
        return "A";
    }
}
