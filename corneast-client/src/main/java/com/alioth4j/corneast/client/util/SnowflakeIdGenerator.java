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

package com.alioth4j.corneast.client.util;

/**
 * Snowflake ID generator.
 *
 * Generates 64‑bit unique IDs with the following layout:
 *
 * | Bits | Meaning                         |
 * |------|---------------------------------|
 * | 1    | Unused sign bit (always 0)      |
 * | 41   | Timestamp in milliseconds since custom epoch |
 * | 5    | Datacenter ID                   |
 * | 5    | Worker (machine) ID             |
 * | 12   | Sequence number within the same millisecond |
 *
 * Reference: Twitter Snowflake (original Scala implementation).
 *
 * Usage notes:
 * 1. Each machine must have a unique combination of datacenterId and workerId
 *    (both in the range 0‑31).
 * 2. Choose a fixed past epoch to keep the timestamp portion positive.
 */
public final class SnowflakeIdGenerator {

    /** Custom epoch (2020‑01‑01 00:00:00 UTC). Change if desired. */
    private static final long EPOCH = 1577836800000L;

    /** Number of bits allocated for each component. */
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    /** Maximum values for each component (derived from bit lengths). */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);      // 31
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS); // 31
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);        // 4095

    /** Bit shifts for each component when constructing the final ID. */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT =
            SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long workerId;
    private final long datacenterId;

    private long sequence = 0L;          // Incremented within the same millisecond
    private long lastTimestamp = -1L;    // Last timestamp when an ID was generated

    /**
     * Constructor.
     *
     * @param workerId     Machine identifier (0‑31)
     * @param datacenterId Datacenter identifier (0‑31)
     */
    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                    String.format("workerId must be between 0 and %d", MAX_WORKER_ID));
        }
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException(
                    String.format("datacenterId must be between 0 and %d", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * Generates the next unique 64‑bit ID.
     *
     * @return a unique ID
     */
    public synchronized long nextId() {
        long timestamp = currentTime();

        // Detect clock rollback
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(
                    String.format("Clock moved backwards. Refusing to generate id for %d ms",
                            lastTimestamp - timestamp));
        }

        if (timestamp == lastTimestamp) {
            // Same millisecond: increment sequence
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence overflow within the millisecond; wait for next millisecond
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // New millisecond: reset sequence
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // Assemble the ID from its components
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT) |
                (datacenterId << DATACENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    /** Returns the current time in milliseconds. */
    private long currentTime() {
        return System.currentTimeMillis();
    }

    /** Spins until the next millisecond. */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime();
        }
        return timestamp;
    }

}

