package com.liquido.core.common.utils;

import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Properties Utils
 */
@Slf4j
public class PropertiesUtils {

    private static Properties properties;

    static {
        properties = new Properties();
    }

    public static void setProperty(final String key, final String value) {
        if (StringUtils.isNotBlank(key)) {
            properties.setProperty(key.trim(), StringUtils.isBlank(value) ? "" : value.trim());
        }
    }

    public static String getProperty(final String key, final String defaultValue) {
        if (StringUtils.isBlank(key)) {
            return null;
        }

        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value.trim();
    }

    public static String getString(final String key) {
        return properties.getProperty(key.trim());
    }

    public static Integer getInteger(final String key) {
        try {
            return Integer.parseInt(properties.getProperty(key.trim()));
        } catch (Exception e) {
            return null;
        }
    }

    public static Long getLong(final String key) {
        try {
            return Long.parseLong(properties.getProperty(key.trim()));
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean getBoolean(final String key) {
        try {
            return Boolean.parseBoolean(properties.getProperty(key.trim()));
        } catch (Exception e) {
            log.error("PropertiesUtils.getBoolean error:", e);
        }
        return Boolean.FALSE;
    }
}
