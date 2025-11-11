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
