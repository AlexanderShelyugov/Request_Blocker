package ru.alexander.request_blocker.blocking.storage.simple;

import lombok.val;
import ru.alexander.request_blocker.blocking.storage.sharding.AbstractShardingCounterStorage;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.ShardingStrategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class SimpleCountersStorage extends AbstractShardingCounterStorage {
    private final Map<Integer, Map<String, Map<String, Integer>>> storageMap = new HashMap<>();

    public SimpleCountersStorage(ShardingStrategy shardingStrategy) {
        super(shardingStrategy);
    }

    @Override
    protected Map<String, Integer> getRelatedShard(int executionID, String ip) {
        val countersForExecution = storageMap.computeIfAbsent(executionID, id -> new HashMap<>());
        val shardName = getShardingStrategy().getShardName(executionID, ip);
        return countersForExecution.computeIfAbsent(shardName, shard -> new HashMap<>());
    }

    @Override
    public void removeAllCounters() {
        // No need to drop already aligned hash table for executions
        val executions = new HashSet<>(storageMap.keySet());
        executions.forEach(execution -> storageMap.put(execution, new HashMap<>()));
    }
}
