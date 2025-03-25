package com.alioth4j.corneast_core.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(RedisConfigProperties.class)
public class RedissonConfig {

    @Bean
    public List<RedissonClient> redissonClients(RedisConfigProperties redisConfigProperties) {
        List<RedissonClient> redissonClients = new ArrayList<>();
        for (RedisNodeProperties node : redisConfigProperties.getNodes()) {
            Config config = new Config();
            config.useSingleServer()
                  .setAddress(node.getAddress())
                  .setDatabase(node.getDatabase());
            RedissonClient redissonClient = Redisson.create(config);
            redissonClients.add(redissonClient);
        }
        return redissonClients;
    }

}
