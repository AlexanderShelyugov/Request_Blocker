package ru.alexander.request_blocker.blocking.storage.sharding;

public interface ShardingStrategy {
    String getShardName(int executionID, String ip);
}
