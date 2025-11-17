package com.alioth4j.corneast_core.shutdown;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Shutdown task of `redissonClients`.
 *
 * @author Alioth Null
 */
@Component
public class RedissonClientsShutdownTask implements ShutdownTask {

    @Autowired(required = false)
    @Qualifier("redissonClients")
    private List<RedissonClient> redissonClients;

    public RedissonClientsShutdownTask() {
    }

    @Override
    public void shutdown() {
        if (redissonClients != null) {
            for (RedissonClient redissonClient : redissonClients) {
                if (redissonClient != null && !redissonClient.isShutdown()) {
                    redissonClient.shutdown();
                }
            }
        }
    }

    @Override
    public String getComponentName() {
        return "RedissonClients";
    }

}
