package com.liquido.core.common.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

/**
 * MD5 encryption processing tool
 */
public class Md5Util {

    /**
     * Used building output as Hex
     */
    private static final char[] hexDigits =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static MessageDigest messageDigest = null;

    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
    }

    public static String getMd5(final byte[] str) {
        messageDigest.update(str);
        final byte[] bytes = messageDigest.digest();
        return new String(encodeHex(bytes));
    }

    public static String getMd5(final String str) {
        messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        final byte[] bytes = messageDigest.digest();
        return new String(encodeHex(bytes));
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

    @SneakyThrows
    public static String getFileMd5(final File file) {
        @Cleanup final InputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[8192];
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            messageDigest.update(buffer, 0, numRead);
        }

        return bufferToHex(messageDigest.digest());
    }

    @SneakyThrows
    public static String getMultiPartFile(final MultipartFile file) {
        return getMd5(file.getBytes());
    }

    public static String getFileMd5(final String filePath) {
        return getFileMd5(new File(filePath));
    }

    private static String bufferToHex(final byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(final byte[] bytes, int m, int n) {
        final StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(final byte bt, final StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

}
