package com.alioth4j.corneast_core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("storage")
public class StorageConfigProperties {

    private List<String> masters;

    private List<String> sentinels;

    private int database;

    private int timeout;

    private int connectTimeout;

    public StorageConfigProperties() {
    }

    public List<String> getMasters() {
        return masters;
    }

    public void setMasters(List<String> masters) {
        this.masters = masters;
    }

    public List<String> getSentinels() {
        return sentinels;
    }

    public void setSentinels(List<String> sentinels) {
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

}
