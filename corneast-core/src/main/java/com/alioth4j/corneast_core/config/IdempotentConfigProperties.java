package com.alioth4j.corneast_core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "idempotent")
public class IdempotentConfigProperties {

    private List<String> redisAddresses;


    public IdempotentConfigProperties() {
    }

    public IdempotentConfigProperties(List<String> redisAddresses) {
        this.redisAddresses = redisAddresses;
    }


    public List<String> getRedisAddresses() {
        return redisAddresses;
    }

    public void setRedisAddresses(List<String> redisAddresses) {
        this.redisAddresses = redisAddresses;
    }

}
