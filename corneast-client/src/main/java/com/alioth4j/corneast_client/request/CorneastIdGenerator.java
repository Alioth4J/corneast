package com.alioth4j.corneast_client.request;

import com.alioth4j.corneast_client.utils.SnowflakeIdGenerator;

import java.util.UUID;

/**
 * Generates unique ids for idempotent id.
 *
 * Provided algorithms:
 * - uuid
 * - snowflake
 *
 * @author Alioth Null
 */
public class CorneastIdGenerator {

    // ========
    // UUID
    // ========

    /**
     * UUID generator.
     * @return uuid string
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    // =============
    // Snowflake
    // =============

    /* Singleton instance of `SnowflakeIdGenerator` in `CorneastIdGenerator` */
    private static volatile SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * Init snowflake with workerId and datacenterId.
     * This method must be invoked before use of `snowflake()`, or `IllegalStateException` will be thrown.
     * @param workerId worker id
     * @param datacenterId datacenter id
     */
    public static void initSnowflake(long workerId, long datacenterId) {
        if (snowflakeIdGenerator == null) {
            synchronized (CorneastIdGenerator.class) {
                if (snowflakeIdGenerator == null) {
                    snowflakeIdGenerator = new SnowflakeIdGenerator(workerId, datacenterId);
                }
            }
        }
    }

    /**
     * Snowflake id generator.
     * @return snowflake string
     */
    public static String snowflake() {
        if (snowflakeIdGenerator == null) {
            throw new IllegalStateException("SnowflakeIdGenerator must be initialized");
        }
        return String.valueOf(snowflakeIdGenerator.nextId());
    }

}
