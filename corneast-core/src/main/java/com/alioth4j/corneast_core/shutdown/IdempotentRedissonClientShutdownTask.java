package com.alioth4j.corneast_core.shutdown;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Shutdown task of `idempotentRedissonClient`.
 *
 * @author Alioth Null
 */
@Component
public class IdempotentRedissonClientShutdownTask implements ShutdownTask {

    @Autowired(required = false)
    @Qualifier("idempotentRedissonClient")
    private RedissonClient idempotentRedissonClient;

    public IdempotentRedissonClientShutdownTask() {
    }

    @Override
    public void shutdown() {
        if (idempotentRedissonClient != null && !idempotentRedissonClient.isShutdown()) {
            idempotentRedissonClient.shutdown();
        }
    }

    @Override
    public String getComponentName() {
        return "IdempotentRedissonClient";
    }

}
