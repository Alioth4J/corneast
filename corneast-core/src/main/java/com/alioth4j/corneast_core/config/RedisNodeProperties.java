package com.alioth4j.corneast_core.config;

import lombok.Data;

/**
 * This class corresponds with the configuration in application.yml
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
