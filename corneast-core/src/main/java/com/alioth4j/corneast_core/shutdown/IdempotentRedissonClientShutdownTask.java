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
