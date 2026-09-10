package com.liquido.aqueducts.util;

public class CommonUtil {

    /**
     * Replace all but the first 6 and last 4 digits of the string with an * symbol
     *
     * @param str
     * @return
     */
    public static String maskString(String str) {
        if (str.length() <= 10) {
            return str;
        }
        int length = str.length();
        String prefix = str.substring(0, 6);
        String suffix = str.substring(length - 4);
        return prefix +
                "*".repeat(length - 10) +
                suffix;
    }
}
