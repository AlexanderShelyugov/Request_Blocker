package ru.alexander.request_blocker.blocking.storage.hazelcast;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import lombok.val;
import ru.alexander.request_blocker.blocking.storage.sharding.AbstractShardingCounterStorage;
import ru.alexander.request_blocker.blocking.storage.sharding.ShardingStrategy;

import java.util.Map;

class HazelcastCountersStorage extends AbstractShardingCounterStorage {
    private final HazelcastInstance hazelcast;

    public HazelcastCountersStorage(ShardingStrategy shardingStrategy,
                                    HazelcastInstance hazelcast) {
        super(shardingStrategy);
        this.hazelcast = hazelcast;
    }

    @Override
    protected Map<String, Integer> getRelatedShard(int executionID, String ip) {
        val shardName = getShardingStrategy().getShardName(executionID, ip);
        return hazelcast.getMap(shardName);
    }

    @Override
    public void removeAllCounters() {
        hazelcast.getDistributedObjects()
            .forEach(DistributedObject::destroy);
    }
}
