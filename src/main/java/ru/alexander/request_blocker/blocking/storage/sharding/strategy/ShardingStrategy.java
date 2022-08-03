package ru.alexander.request_blocker.blocking.storage.sharding.strategy;

/**
 * Provides a shard name by executionID and IP address.
 */
public interface ShardingStrategy {
    String getShardName(int executionID, String ip);
}
