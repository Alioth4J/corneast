package com.alioth4j.corneast_core.config;

import lombok.Data;

/**
 * Configuration class of a Redis node, corresponding with application.yml
 * Currently used for idempotence.
 *
 * @author Alioth Null
 */
@Data
public class RedisNodeProperties {

    private String id;

    private String address;

    private int database;

    private int timeout;

    private int connectTimeout;

    private int connectionPoolSize;

}
