package com.alioth4j.corneast_core.util;

// maybe needless to use
public final class SnowflakeIdUtil {

    // Prevent instantiation of the util class.
    private SnowflakeIdUtil() { }

    // Instance of the underlying Snowflake generator.
    private static volatile SnowflakeIdGenerator idGenerator;

    /**
     * Initialize the SnowflakeIdUtil with specific workerId and datacenterId.
     * This should be called once in your application start-up code before calling nextId().
     *
     * @param workerId     Worker ID (range: 0 - 31)
     * @param datacenterId Datacenter ID (range: 0 - 31)
     */
    public static void init(long workerId, long datacenterId) {
        if (idGenerator == null) {
            synchronized (SnowflakeIdUtil.class) {
                if (idGenerator == null) {
                    idGenerator = new SnowflakeIdGenerator(workerId, datacenterId);
                }
            }
        }
    }

    /**
     * Get the next unique Snowflake ID.
     *
     * @return a unique long ID.
     */
    public static long nextId() {
        if (idGenerator == null) {
            throw new IllegalStateException("SnowflakeIdUtil is not initialized. Call init() first.");
        }
        return idGenerator.nextId();
    }


    //
    // Inner static class implementing the Snowflake algorithm logic.
    //
    private static class SnowflakeIdGenerator {

        // Custom epoch, 2025-01-01.
        private final long twepoch = 1735689600000L;

        // Bit lengths for different parts of the ID.
        private final long workerIdBits = 5L;
        private final long datacenterIdBits = 5L;
        private final long sequenceBits = 12L;

        // Maximum values for worker and datacenter IDs.
        private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
        private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

        // Bit shifts for each part.
        private final long workerIdShift = sequenceBits;
        private final long datacenterIdShift = sequenceBits + workerIdBits;
        private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

        // Mask for sequence to avoid overflow.
        private final long sequenceMask = -1L ^ (-1L << sequenceBits);

        private final long workerId;
        private final long datacenterId;

        // Sequence within the same millisecond.
        private long sequence = 0L;

        // Last timestamp an ID was generated.
        private long lastTimestamp = -1L;

        /**
         * Constructor.
         *
         * @param workerId     Worker ID (0 to maxWorkerId)
         * @param datacenterId Datacenter ID (0 to maxDatacenterId)
         */
        public SnowflakeIdGenerator(long workerId, long datacenterId) {
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException(
                        String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
            }
            if (datacenterId > maxDatacenterId || datacenterId < 0) {
                throw new IllegalArgumentException(
                        String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
            }
            this.workerId = workerId;
            this.datacenterId = datacenterId;
        }

        /**
         * Generate next ID (thread-safe).
         *
         * @return a unique Snowflake ID.
         */
        public synchronized long nextId() {
            long timestamp = timeGen();

            if (timestamp < lastTimestamp) {
                throw new RuntimeException(
                        String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp)
                );
            }

            if (lastTimestamp == timestamp) {
                // Same millisecond: increment sequence.
                sequence = (sequence + 1) & sequenceMask;
                if (sequence == 0) {
                    // Sequence exhausted in this millisecond, wait for next.
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                // New millisecond: start sequence over.
                sequence = 0L;
            }

            lastTimestamp = timestamp;

            return ((timestamp - twepoch) << timestampLeftShift)
                    | (datacenterId << datacenterIdShift)
                    | (workerId << workerIdShift)
                    | sequence;
        }

        /**
         * Wait until the next millisecond.
         *
         * @param lastTimestamp Last timestamp used.
         * @return current timestamp in millis.
         */
        private long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        /**
         * Get current system time in milliseconds.
         *
         * @return current system time in millis.
         */
        private long timeGen() {
            return System.currentTimeMillis();
        }
    }

}
