/*
 * Corneast
 * Copyright (C) 2026 Alioth Null
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

package com.alioth4j.corneast.core.redis;

import org.redisson.api.RKeys;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisGC {

    private static final Logger log = LoggerFactory.getLogger(RedisGC.class);

    @Autowired
    @Qualifier("redissonClients")
    private List<RedissonClient> redissonClients;

    private final int batchSize = 10000;

    private final String luaScript = """
                                     if redis.call('GET', KEYS[1]) == '0' then
                                         return redis.call('DEL', KEYS[1])
                                     else
                                         return 0
                                     end
                                     """;

    @Scheduled(fixedRate = 10000L)
    public void gc() {
        long count = 0;
        for (RedissonClient redissonClient : redissonClients) {
            RKeys rKeys = redissonClient.getKeys();
            for (String key : rKeys.getKeys()) {
                Long result = redissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.INTEGER, List.of(key));
                // LongCache
                if (result != null && result == 1) {
                    count++;
                }
            }
        }
        if (count > 0) {
            log.info("Deleted {} keys in all RedissonClients", count);
        }
    }

}
