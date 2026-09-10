package com.liquido.core.common.snowflake;

import org.springframework.util.Assert;

/**
 * Snowflake Util
 */
public class SnowflakeIdUtil {

    /**
     * Whether the current operating environment is K8S
     */
    public static boolean K8S_ENVIRONMENT = false;

    /**
     * Get Snowflake Algorithm
     *
     * @return
     */
    public static long generate() {
        if (K8S_ENVIRONMENT) {
            return SnowflakeIdK8S.generate();
        }

        return SnowflakeId.generate();
    }

    public static String generateWithPrefix(final String prefix) {
        Assert.notNull(prefix, "prefix cannot be empty");

        if (K8S_ENVIRONMENT) {
            return prefix + SnowflakeIdK8S.generate();
        }

        return prefix + SnowflakeId.generate();
    }

    /**
     * Get the timestamp corresponding to the snowflake algorithm
     *
     * @param snowflakeId
     * @return
     */
    public static long getTimestampBySnowflakeId(final long snowflakeId) {
        if (K8S_ENVIRONMENT) {
            return SnowflakeIdK8S.getTimestampBySnowflakeId(snowflakeId);
        } else {
            return SnowflakeId.getTimestampBySnowflakeId(snowflakeId);
        }
    }
}
