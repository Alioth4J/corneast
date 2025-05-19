package com.alioth4j.corneast_core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration class of idempotence, corresponding with application.yml
 *
 * @author Alioth Null
 */
@ConfigurationProperties(prefix = "idempotent")
public class IdempotentConfigProperties {

    private RedisNodeProperties redis;

    public RedisNodeProperties getRedis() {
        return redis;
    }

    public void setRedis(RedisNodeProperties redis) {
        this.redis = redis;
    }

}
