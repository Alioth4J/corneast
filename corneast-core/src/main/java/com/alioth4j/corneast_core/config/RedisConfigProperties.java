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

package com.alioth4j.corneast_core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "redis-config")
public class RedisConfigProperties {

    private List<RedisSentinelConfigProperties> sentinels = new ArrayList<>();

    private int database;

    private int timeout;

    private int connectTimeout;

    private int connectionPoolSize;


    public RedisConfigProperties() {
    }

    public RedisConfigProperties(List<RedisSentinelConfigProperties> sentinels,
                                 int database,
                                 int timeout,
                                 int connectTimeout,
                                 int connectionPoolSize) {
        this.sentinels = sentinels;
        this.database = database;
        this.timeout = timeout;
        this.connectTimeout = connectTimeout;
        this.connectionPoolSize = connectionPoolSize;
    }


    public List<RedisSentinelConfigProperties> getSentinels() {
        return sentinels;
    }

    public void setSentinels(List<RedisSentinelConfigProperties> sentinels) {
        this.sentinels = sentinels;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

}
