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

package com.alioth4j.corneast_client.request;

import com.alioth4j.corneast_client.util.SnowflakeIdGenerator;

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
