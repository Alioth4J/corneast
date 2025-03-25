package com.alioth4j.corneast_core.config;

/**
 * This class corresponds with the configuration in application.yml
 */
public class RedisNodeProperties {

    private String id;

    private String address;

    private int database;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

}
