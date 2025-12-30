/*
 * Corneast
 * Copyright (C) 2025 Alioth Null
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alioth4j.corneast.core.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
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
@EnableConfigurationProperties({StorageConfigProperties.class, IdempotentConfigProperties.class})
public class RedissonConfig {

    @Bean
    public List<RedissonClient> redissonClients(StorageConfigProperties storageConfigProperties) {
        List<String> masters = storageConfigProperties.getMasters();
        List<String> sentinels = storageConfigProperties.getSentinels();
        String[] sentinelArray = sentinels.toArray(new String[sentinels.size()]);
        int database = storageConfigProperties.getDatabase();
        int timeout = storageConfigProperties.getTimeout();
        int connectTimeout = storageConfigProperties.getConnectTimeout();
        List<RedissonClient> redissonClients = new ArrayList<>();
        for (String master : masters) {
            Config config = new Config();
            config.useSentinelServers()
                    .setMasterName(master)
                    .addSentinelAddress(sentinelArray)
                    .setDatabase(database)
                    .setTimeout(timeout)
                    .setConnectTimeout(connectTimeout)
                    .setReadMode(ReadMode.SLAVE);
            RedissonClient redissonClient = Redisson.create(config);
            redissonClients.add(redissonClient);
        }
        return redissonClients;
    }

    /**
     * Idempotent redis nodes
     */
    @Bean
    public RedissonClient idempotentRedissonClient(IdempotentConfigProperties idempotentConfigProperties) {
        Config config = new Config();
        List<String> redisEndpoints = idempotentConfigProperties.getRedisEndpoints();
        config.useClusterServers()
              .addNodeAddress(redisEndpoints.toArray(new String[redisEndpoints.size()]));
        return Redisson.create(config);
    }

}
