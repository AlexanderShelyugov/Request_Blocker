package ru.alexander.request_blocker.blocking.storage.hazelcast;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.alexander.request_blocker.blocking.storage.api.CountersStorage;

import java.util.Map;

@Repository
@Primary
@RequiredArgsConstructor
class HazelcastCountersStorage implements CountersStorage {
    private final HazelcastInstance hazelcast;
    private final ShardingStrategy shardingStrategy;

    @Override
    public int getCounterOrZero(int executionID, String ip) {
        val ipCounters = getRelatedShard(executionID, ip);
        return ipCounters.computeIfAbsent(ip, key -> 0);
    }

    @Override
    public void setCounter(int executionID, String ip, int newValue) {
        val ipCounters = getRelatedShard(executionID, ip);
        ipCounters.put(ip, newValue);
    }

    @Override
    public void removeAllCounters() {
        hazelcast.getDistributedObjects()
            .forEach(DistributedObject::destroy);
    }

    private Map<String, Integer> getRelatedShard(int executionID, String ip) {
        val shardName = shardingStrategy.getShardName(executionID, ip);
        return hazelcast.getMap(shardName);
    }

}
