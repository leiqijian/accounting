package com.liquido.aqueducts.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

    private static final char[] hexDigits =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
    }

    private static char[] encodeHex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = hexDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = hexDigits[0x0F & data[i]];
        }
        return out;
    }

    public static String getMd5(final String str) {
        messagedigest.update(str.getBytes(StandardCharsets.UTF_8));
        final byte[] bytes = messagedigest.digest();
        return new String(encodeHex(bytes));
    }

}
