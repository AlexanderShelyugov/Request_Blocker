package ru.alexander.request_blocker.blocking.storage.sharding;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;
import ru.alexander.request_blocker.blocking.storage.sharding.strategy.ShardingStrategy;

import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

/**
 * Common template for sharding storages.
 */
@RequiredArgsConstructor
public abstract class AbstractShardingCounterStorage implements CountersStorage {

    @Getter(PROTECTED)
    @NonNull
    private final ShardingStrategy shardingStrategy;

    @Override
    public final int getCounterOrZero(int executionID, String ip) {
        val ipCounters = getRelatedShard(executionID, ip);
        return ipCounters.computeIfAbsent(ip, key -> 0);
    }

    @Override
    public final void setCounter(int executionID, String ip, int newValue) {
        val ipCounters = getRelatedShard(executionID, ip);
        ipCounters.put(ip, newValue);
    }

    protected abstract Map<String, Integer> getRelatedShard(int executionID, String ip);
}
