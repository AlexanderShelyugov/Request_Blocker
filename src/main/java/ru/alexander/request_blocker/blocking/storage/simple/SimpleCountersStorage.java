package ru.alexander.request_blocker.blocking.storage.simple;

import lombok.val;
import ru.alexander.request_blocker.blocking.storage.sharding.AbstractShardingCounterStorage;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.ShardingStrategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Counter storage that works with plain java code.
 */
class SimpleCountersStorage extends AbstractShardingCounterStorage {
    private final Map<String, Map<String, Integer>> shardsByName = new ConcurrentHashMap<>();

    public SimpleCountersStorage(ShardingStrategy shardingStrategy) {
        super(shardingStrategy);
    }

    @Override
    protected Map<String, Integer> getRelatedShard(int executionID, String ip) {
        val shardName = getShardingStrategy().getShardName(executionID, ip);
        return shardsByName.computeIfAbsent(shardName, shard -> new HashMap<>());
    }

    @Override
    public void removeAllCounters() {
        // No need to drop already aligned hash table for executions
        val executions = new HashSet<>(shardsByName.keySet());
        executions.forEach(execution -> shardsByName.put(execution, new HashMap<>()));
    }
}
