package com.liquido.core.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.liquido.core.common.exception.CommonExceptionCode;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.UnixCrypt;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Sha256Util
 */
@Slf4j
public class Sha256Util {

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * @return Salt needed to generate password
     */
    public static String getSalt() {
        return RandomStringUtils.randomAlphabetic(32);
    }


    /**
     * FeiShu, Lark robot sign
     *
     * @param secretKey secretKey
     * @param timestamp timestamp second
     * @return sign
     */
    public static String getRobotSign(final String secretKey, final long timestamp) {
        if (StringUtils.isBlank(secretKey) || timestamp <= 0) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL.exception();
        }

        byte[] message = {};
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec((timestamp + "\n" + secretKey).getBytes(), HMAC_SHA256));
            return Base64.encodeBase64String(mac.doFinal(message));
        } catch (Exception e) {
            log.error("sign fail", e);
        }

        return "";
    }


    /**
     * HmacSha256
     *
     * @param plainText plainText
     * @param salt      salt
     * @return sha256
     */
    @SuppressWarnings("UnstableApiUsage")
    public static String hmacSha256(String plainText, final String salt) {
        final SecretKeySpec secretKey =
                new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
        final byte[] data = Hashing.hmacSha256(secretKey).newHasher()
                .putString(plainText, StandardCharsets.UTF_8).hash().asBytes();
        return byte2Hex(data);
    }

    public static String crypt(final String content, final String salt) {
        return UnixCrypt.crypt(content, salt);
    }

    /**
     * sha256_HMAC
     *
     * @param plainText plainText
     * @param salt      salt
     * @return signature
     */
    public static String getSignature(String plainText, final String salt) {
        try {
            return hmacSha256(plainText, salt);
        } catch (Exception e) {
            log.error("generate signature value failed", e);
        }
        return "";
    }

    /**
     * check signature
     *
     * @param content content
     * @param secret  secret
     * @param sign    sign
     * @return boolean
     */
    public static boolean checkSignature(final String content,
                                         final String secret,
                                         final String sign) {
        return getSignature(content, secret).equals(sign);
    }

    public static String getSha256(final String str) {
        String encodeStr = "";
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error("Sha256Util generate getSha256 error:", e);
        }
        return encodeStr;
    }

    private static String byte2Hex(final byte[] bytes) {
        final StringBuilder stringBuilder = new StringBuilder();
        String temp;
        for (byte data : bytes) {
            temp = Integer.toHexString(data & 0xFF);
            if (temp.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }


    public static byte[] decryptKey(String keyStr) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(keyStr.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }


    public static String decrypt(String keyStr, String content) throws Exception {
        byte[] decode = java.util.Base64.getDecoder().decode(content);
        Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
        byte[] iv = new byte[16];
        System.arraycopy(decode, 0, iv, 0, 16);
        byte[] data = new byte[decode.length - 16];
        System.arraycopy(decode, 16, data, 0, data.length);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(
                        Objects.requireNonNull(decryptKey(keyStr)), "AES"),
                new IvParameterSpec(iv));
        byte[] r = cipher.doFinal(data);
        if (r.length > 0) {
            int p = r.length - 1;
            if (p != r.length - 1) {
                byte[] rr = new byte[p + 1];
                System.arraycopy(r, 0, rr, 0, p + 1);
                r = rr;
            }
        }
        return new String(r, StandardCharsets.UTF_8);
    }


    public static String encrypt(String key, String source) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            messageDigest.update(key.getBytes());
            SecretKeySpec keySpec = new SecretKeySpec(messageDigest.digest(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] bytes = source.getBytes();
            byte[] newSrc = new byte[bytes.length + 16];
            byte[] src = new byte[16];
            System.arraycopy(src, 0, newSrc, 0, src.length);
            System.arraycopy(bytes, 0, newSrc, 16, bytes.length);
            IvParameterSpec iv = new IvParameterSpec(src);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] encrypted = cipher.doFinal(newSrc);
            return java.util.Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("encrypt error:", e);
        }
        return null;
    }
}
