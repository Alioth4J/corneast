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

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "idempotent")
public class IdempotentConfigProperties {

    private List<String> redisEndpoints;

    public IdempotentConfigProperties() {
    }

    public IdempotentConfigProperties(List<String> redisEndpoints) {
        this.redisEndpoints = redisEndpoints;
    }

    public List<String> getRedisEndpoints() {
        return redisEndpoints;
    }

    public void setRedisEndpoints(List<String> redisEndpoints) {
        this.redisEndpoints = redisEndpoints;
    }

}
