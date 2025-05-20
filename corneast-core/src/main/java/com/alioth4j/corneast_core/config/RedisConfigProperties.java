package com.alioth4j.corneast_core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "redis-config")
@Data
public class RedisConfigProperties {

    private List<RedisSentinelConfigProperties> sentinels = new ArrayList<>();

    private int database;

    private int timeout;

    private int connectTimeout;

    private int connectionPoolSize;
    
}
