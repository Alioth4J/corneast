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

    @Bean
    public List<RedissonClient> redissonClients(RedisConfigProperties redisConfigProperties) {
        List<RedisSentinelConfigProperties> sentinels = redisConfigProperties.getSentinels();
        int database = redisConfigProperties.getDatabase();
        int timeout = redisConfigProperties.getTimeout();
        int connectTimeout = redisConfigProperties.getConnectTimeout();
        int connectionPoolSize = redisConfigProperties.getConnectionPoolSize();
        List<RedissonClient> redissonClients = new ArrayList<>();
        for (RedisSentinelConfigProperties sentinel : sentinels) {
            String master = sentinel.getMaster();
            List<String> nodes = sentinel.getNodes();
            Config config = new Config();
            config.useSentinelServers()
                    .setMasterName(master)
                    .addSentinelAddress(nodes.toArray(new String[0]))
                    .setDatabase(database)
                    .setTimeout(timeout)
                    .setConnectTimeout(connectTimeout)
                    .setMasterConnectionPoolSize(connectionPoolSize)
                    .setCheckSentinelsList(false);
            redissonClients.add(Redisson.create(config));
        }
        return redissonClients;
    }

//    /**
//     * Redis nodes
//     */
//    @Bean
//    public List<RedissonClient> redissonClients(RedisConfigProperties redisConfigProperties) {
//        List<RedissonClient> redissonClients = new ArrayList<>();
//        for (RedisNodeProperties node : redisConfigProperties.getNodes()) {
//            Config config = new Config();
//            config.useSingleServer()
//                  .setAddress(node.getAddress())
//                  .setDatabase(node.getDatabase())
//                  .setTimeout(node.getTimeout())
//                  .setConnectTimeout(node.getConnectTimeout())
//                  .setConnectionPoolSize(node.getConnectionPoolSize());
//            RedissonClient redissonClient = Redisson.create(config);
//            redissonClients.add(redissonClient);
//        }
//        return redissonClients;
//    }

    /**
     * Idempotent redis nodes
     */
    @Bean
    public RedissonClient idempotentRedissonClient(IdempotentConfigProperties idempotentConfigProperties) {
        Config config = new Config();
        config.useClusterServers()
              .addNodeAddress(
       "redis://127.0.0.1:6000",
                  "redis://127.0.0.1:6001",
                  "redis://127.0.0.1:6002");
        return Redisson.create(config);
    }


}
