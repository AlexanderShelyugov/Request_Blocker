package ru.alexander.request_blocker.blocking.storage.hazelcast;

public interface ShardingStrategy {
    String getShardName(int executionID, String ip);
}
