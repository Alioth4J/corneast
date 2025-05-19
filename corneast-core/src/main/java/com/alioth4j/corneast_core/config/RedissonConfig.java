package com.alioth4j.corneast_core.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class of RedissonClient.
 *
 * The count of RedissonClient is equal to the count of Redis nodes.
 *
 * @author Alioth Null
 */
@Configuration
@EnableConfigurationProperties({RedisConfigProperties.class, IdempotentConfigProperties.class})
public class RedissonConfig {

    /**
     * Redis nodes
     */
    @Bean
    public List<RedissonClient> redissonClients(RedisConfigProperties redisConfigProperties) {
        List<RedissonClient> redissonClients = new ArrayList<>();
        for (RedisNodeProperties node : redisConfigProperties.getNodes()) {
            Config config = new Config();
            config.useSingleServer()
                  .setAddress(node.getAddress())
                  .setDatabase(node.getDatabase())
                  .setTimeout(node.getTimeout())
                  .setConnectTimeout(node.getConnectTimeout())
                  .setConnectionPoolSize(node.getConnectionPoolSize());
            RedissonClient redissonClient = Redisson.create(config);
            redissonClients.add(redissonClient);
        }
        return redissonClients;
    }

    /**
     * Idempotent redis node
     */
    @Bean
    public RedissonClient idempotentRedissonClient(IdempotentConfigProperties idempotentConfigProperties) {
        RedisNodeProperties redisNodeProperties = idempotentConfigProperties.getRedis();
        Config config = new Config();
        config.useSingleServer()
                .setAddress(redisNodeProperties.getAddress())
                .setDatabase(redisNodeProperties.getDatabase())
                .setTimeout(redisNodeProperties.getTimeout())
                .setConnectTimeout(redisNodeProperties.getConnectTimeout())
                .setConnectionPoolSize(redisNodeProperties.getConnectionPoolSize());
        return Redisson.create(config);
    }

}
