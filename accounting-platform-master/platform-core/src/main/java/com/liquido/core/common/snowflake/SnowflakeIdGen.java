package com.liquido.core.common.snowflake;

/**
 * SnowflakeIdGen
 */
public class SnowflakeIdGen {
    /**
     * base time 2022-01-01 00:00:00
     * 1640966400000
     */
    private final long twepoch = 1640966400000L;

    private final long workerIdBits = 5L;

    private final long dataCenterIdBits = 5L;

    private final long sequenceBits = 12L;

    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    private final long workerIdShift = sequenceBits;

    private final long dataCenterIdShift = sequenceBits + workerIdBits;

    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;

    private long dataCenterId;

    private long sequence = 0L;

    private long lastTimestamp = -1L;

    /**
     * SnowflakeIdGen
     *
     * @param workerId     workerId (0~31)
     * @param dataCenterId dataCenterId (0~31)
     */
    public SnowflakeIdGen(final long workerId, final long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0",
                            maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0",
                            maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * generate snowflake id
     *
     * @return
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format(
                            "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift) | sequence;
    }

    /**
     * Block until the next millisecond until a new timestamp is obtained
     * Prevent the generated time from being smaller
     * than the previous time (due to NTP callbacks, etc.), and keep the incremental trend.
     *
     * @param lastTimestamp The last time the ID was generated
     * @return current timestamp
     */
    protected long tilNextMillis(final long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
