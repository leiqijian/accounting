package com.liquido.core.common.security;

import java.util.Base64;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * AesUtil
 */
@Slf4j
public class AesUtil {
    private static final String ALGORITHM = "AES";
    private static String key = null; // 16-byte key

    public static void setKey(final String val) {
        key = val;
    }

    /**
     * encrypt data and use base64 to encode the encrypted data.
     * @param plainText     text to encrypt
     * @return              encrypted data with base64 encoding format
     * @throws Exception    thrown if encrypt failed
     * @see Base64.Encoder#getEncoder()
     */
    public static String encrypt(final String plainText) throws Exception {
        return encryptAsString(
                plainText,
                Base64.getEncoder()::encodeToString
        );
    }

    /**
     * encrypt data and use base64 url encoder to encode the encrypted data.
     * @param plainText     text to encrypt
     * @return              encrypted data with base64 encoding format
     * @throws Exception    thrown if encrypt failed
     * @see Base64.Encoder#getUrlEncoder()
     */
    public static String encryptWithUrlEncode(final String plainText) throws Exception {
        return encryptAsString(
                plainText,
                Base64.getUrlEncoder()::encodeToString
        );
    }

    /**
     * decrypt the base64 encoding format's encrypted data.
     * @param base64EncryptedText       encrypted text with base64 encoding format
     * @return                          decrypt text data
     * @throws Exception                thrown if decrypt failed
     * @see Base64.Decoder#getDecoder()
     */
    public static String decrypt(final String base64EncryptedText) throws Exception {
        return decryptAsString(
                base64EncryptedText,
                Base64.getDecoder()::decode
        );
    }

    /**
     * decrypt the base64 url encoding format's encrypted data.
     * @param base64UrlEncryptedText    encrypted text with base64 url encoding format
     * @return                          decrypt text data
     * @throws Exception                thrown if decrypt failed
     * @see Base64.Decoder#getUrlDecoder()
     */
    public static String decryptWithUrlDecode(
            final String base64UrlEncryptedText) throws Exception {
        return decryptAsString(
                base64UrlEncryptedText,
                Base64.getUrlDecoder()::decode
        );
    }

    public static String encryptAsString(final String plainText,
                                         final Function<byte[], String> encoder) throws Exception {
        if (StringUtils.isBlank(plainText) || null == encoder) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL
                    .exception("blank plainText or null encoder");
        }

        final byte[] encrypted = encrypt(plainText.getBytes());
        return encoder.apply(encrypted);
    }

    public static String decryptAsString(final String encryptedText,
                                         final Function<String, byte[]> decoder) throws Exception {
        if (StringUtils.isBlank(encryptedText) || null == decoder) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL
                    .exception("blank encryptedText or null decoder");
        }

        final byte[] encryptedTextBytes = decoder.apply(encryptedText);
        final byte[] decrypted = decrypt(encryptedTextBytes);
        return new String(decrypted);
    }

    public static byte[] encrypt(final byte[] dataToEncrypt) throws Exception {
        if (ArrayUtils.isEmpty(dataToEncrypt)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL
                    .exception("empty dataToEncrypt");
        }

        final SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(dataToEncrypt);
    }

    public static byte[] decrypt(final byte[] encryptedData) throws Exception {
        if (ArrayUtils.isEmpty(encryptedData)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL
                    .exception("empty encryptedData");
        }

        final SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }

    @SneakyThrows
    public static void main(String[] args) {
        log.info(encrypt("1vB$p!mh!cQx9Pkb"));
        log.info(encrypt("h^6oCoo#O03XLJ8A"));

        final String encrypted1 = encryptWithUrlEncode("1vB$p!mh!cQx9Pkb");
        final String decrypted1 = decryptWithUrlDecode(encrypted1);
        log.info(encrypted1);
        log.info(decrypted1);

        final String encrypted2 = encryptWithUrlEncode(
                "382009123123_" + UUID.randomUUID());
        final String decrypted2 = decryptWithUrlDecode(encrypted2);
        log.info(encrypted2);
        log.info(decrypted2);
    }
}
