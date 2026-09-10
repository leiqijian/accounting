package com.liquido.core.common.snowflake;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import com.liquido.core.common.exception.FrameworkExceptionCode;
import com.liquido.core.common.logger.LogWrapper;
import com.liquido.core.configuration.CommonProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Snowflake algorithm generates ID (non-K8S container environment)
 */
@Slf4j
public class SnowflakeId {

    /**
     * Area flag bits
     */
    private static final long regionIdBits = 3L;
    /**
     * machine identification number
     */
    private static final long workerIdBits = 10L;
    /**
     * Serial number identification digits
     */
    private static final long sequenceBits = 10L;
    /**
     * The maximum value of the area flag ID
     */
    private static final long maxRegionId = -1L ^ (-1L << regionIdBits);
    /**
     * Machine ID Max
     */
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    /**
     * Serial Number ID Maximum
     */
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);
    /**
     * Machine ID offset left by 10 bits
     */
    private static final long workerIdShift = sequenceBits;
    /**
     * The business ID is shifted left by 20 bits
     */
    private static final long regionIdShift = sequenceBits + workerIdBits;
    /**
     * Time milliseconds shifted left by 23 bits
     */
    private static final long timestampLeftShift = sequenceBits + workerIdBits + regionIdBits;
    /**
     * base time
     * 2022-01-01 00:00:00
     * 1640966400000
     */
    private static final long twepoch = 1640966400000L;

    private static long lastTimestamp = -1L;

    private static long sequence = 0L;

    /**
     * The number of machines, the value cannot exceed 1023, starts from 0,
     * and can be extended to the 1024th machine at most
     */
    private static long workerId = 0L;

    /**
     * The number of computer rooms, the value cannot exceed 7, starts from 0,
     * and can be expanded to 8 computer rooms at most
     */
    private static long centerId = 0L;

    /**
     * Snowflake algorithm is not enabled by default
     */
    private static boolean enabled = false;

    public static void init(final CommonProperties commonProperties) {
        log.info(LogWrapper.op("SnowflakeIdUtil.init")
                .wrap("CommonProperties.getSnowFlake().getMode()",
                        commonProperties.getSnowFlake().getMode()).toString());
        try {
            switch (commonProperties.getSnowFlake().getMode()) {
                case HOST:
                    workerId = generateWorkerIdByHost();
                    break;
                case IPV4:
                    workerId = generateWorkerIdByIpv4();
                    break;
                case CONFIG:
                    workerId = commonProperties.getSnowFlake().getWorkerId();
                    centerId = commonProperties.getSnowFlake().getCenterId();
                    break;
                default:
                    workerId = generateWorkerIdByHost();
                    break;
            }
        } catch (Exception e) {
            throw FrameworkExceptionCode.SNOWFLAKE_CENTER_ID_ERROR.exception(e);
        }

        if (workerId > maxWorkerId) {
            throw FrameworkExceptionCode.SNOWFLAKE_CENTER_ID_ERROR.exception();
        }

        if (centerId > maxRegionId) {
            throw FrameworkExceptionCode.SNOWFLAKE_CENTER_ID_ERROR.exception();
        }

        enabled = true;
        log.info(LogWrapper.op("SnowflakeId.init")
                .wrap("enabled", enabled)
                .wrap("centerId", centerId)
                .wrap("workerId", workerId).toString());
    }

    public static long generate() {
        if (!enabled) {
            throw FrameworkExceptionCode.SNOWFLAKE_SWITCH_OFF.exception();
        }
        return nextId(false, 0);
    }

    /**
     * generate snowflake id
     *
     * @param isPadding
     * @param busId
     * @return
     */
    private static synchronized long nextId(final boolean isPadding, final long busId) {

        long timestamp = timeGen();
        long paddingNum = centerId;

        if (isPadding) {
            paddingNum = busId;
        }

        if (timestamp < lastTimestamp) {
            long delay = lastTimestamp - timestamp;
            if (delay < 5) {
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException e) {
                    log.error("SnowflakeId generate nextId error:", e);
                }

                timestamp = timeGen();
            }
            if (timestamp < lastTimestamp) {
                try {
                    throw new Exception("Clock moved backwards.  Refusing to generate id for "
                            + (lastTimestamp - timestamp) + " milliseconds");
                } catch (Exception e) {
                    log.error("SnowflakeId generate nextId error:", e);
                }
            } else {
                return generate();
            }
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tailNextMillis(lastTimestamp);
            }
        } else {
            sequence = new SecureRandom().nextInt(10);
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (paddingNum << regionIdShift)
                | (workerId << workerIdShift) | sequence;
    }

    /**
     * To prevent the generated time from being smaller
     * than the previous time (due to NTP callbacks, etc.), keep the incremental trend.
     */
    private static long tailNextMillis(final long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * Get the current timestamp
     *
     * @return
     */
    protected static long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * @return
     */
    private static long generateWorkerIdByIpv4() {
        final Long workerId;
        final InetAddress address;
        try {
            address = InetAddress.getLocalHost();
        } catch (final UnknownHostException e) {
            throw FrameworkExceptionCode.SNOWFLAKE_NET_ADDRESS_ERROR.exception();
        }

        byte[] ipAddressByteArray = address.getAddress();

        workerId =
                (long) (((ipAddressByteArray[ipAddressByteArray.length - 2] & 0B11) << Byte.SIZE)
                        + (ipAddressByteArray[ipAddressByteArray.length - 1] & 0xFF));

        return workerId;
    }

    /**
     * According to the hostname, take the number after "-"
     *
     * @return
     */
    private static long generateWorkerIdByHost() {
        final InetAddress address;
        final Long workerId;
        try {
            address = InetAddress.getLocalHost();
        } catch (final UnknownHostException e) {
            throw new IllegalStateException(
                    "Cannot get LocalHost InetAddress, please check your network!");
        }

        final String hostName = address.getHostName();
        log.info(LogWrapper.op("SnowflakeIdUtil.generateWorkerIdByHost").wrap("hostName", hostName)
                .toString());

        try {
            final String lastHostName = hostName.substring(hostName.lastIndexOf("-") + 1);
            log.info(LogWrapper.op("SnowflakeIdUtil.generateWorkerIdByHost")
                    .wrap("lastHostName", lastHostName).toString());

            workerId = Long.valueOf(lastHostName);
            log.info(LogWrapper.op("SnowflakeIdUtil.generateWorkerIdByHost")
                    .wrap("workerId", workerId).toString());
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Wrong hostname:%s, hostname must be end with number!",
                            hostName));
        }

        return workerId;
    }

    /**
     * Get the timestamp corresponding to the snowflake algorithm
     *
     * @param snowflakeId
     * @return
     */
    public static long getTimestampBySnowflakeId(final long snowflakeId) {
        long result = (snowflakeId >> timestampLeftShift) + twepoch;
        return result;
    }

}
