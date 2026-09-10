package com.liquido.core.common.security;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * RSA encryption and decryption; plaintext length <= key length (Bytes)-11 for example: 117 =
 * 1024/8-11;
 * This RSA uses 1024
 */
@Slf4j
public class RsaEncryptor {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final BouncyCastleProvider DEFAULT_PROVIDER = new BouncyCastleProvider();
    private static KeyFactory KEY_FACTORY = null;
    private static Cipher CIPHER = null;
    private static RSAPrivateKey defaultPrivateKey;
    private static RSAPublicKey defaultPublicKey;

    private static String defaultPublicKeyStr =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyLMukN3BJ1YqhIs4KsxSqy05PsgJiGIFB3A2Gc3"
                    + "KpeJ3HnTpece6W2ioEImrlKgfUcTMkQIWuNnupeG+EcskiXpx2WnG06QBOWd9Q1493DG44ng"
                    + "LEHTdKAkLNJbJFWZ1NoOBs4VdBKszNNFSgTZ9cLWnZuaJgYg7Cr9jZtkjejQIDAQAB";

    private static String defaultPrivateKeyStr =
            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALIsy6Q3cEnViqEi"
                    + "zgqzFKrLTk+yAmIYgUHcDYZzcql4ncedOl5x7pbaKgQiauUqB9RxMyRAha42e6l4"
                    + "b4RyySJenHZacbTpAE5Z31DXj3cMbjieAsQdN0oCQs0lskVZnU2g4GzhV0EqzM00"
                    + "VKBNn1wtadm5omBiDsKv2Nm2SN6NAgMBAAECgYEAikA/8zghJOlRHB8JiTvTeYMv"
                    + "TJ9vQfaJtTSuOfkyq1Uv+EeDlu0ImHzHxVV/ZDnswWbkq+h00ezsn18YCMmYTorG"
                    + "A/ypjwAatMTvcL23DbA8ET8TLsoPHo5FF1WxfvI87nscJ2Sn8CGA295UkULB21sr"
                    + "jyJrAttx5XFwglum+nECQQDjqBoKCi3aueZ/vuyCE5dpiIIOeLa8Rlp0Oz1xFB6M"
                    + "cBP5rBwqqfgvl83zQhvv9lXMqK/GtfypBuNALzWKuCBrAkEAyFubysnbiqwINChM"
                    + "GnvgZ7TLEHk1XT8ant2g2R8qTn8JpYo04V7T8ESJLPk6KjcKI2xQzZlWs7X4wGbU"
                    + "A7pa5wJBANCG5AMaO9JDwex1d7HWPDTAg1C6JbfjQWuB78+qPCkraECj89Doi5sc"
                    + "k9skTO7KVuD2CrwbamlxE6txErwT2C0CQCmBEJzJg/kN+gUT8+/oiWvyP76B3VQb"
                    + "pmASeSeBFTp9hnoMTzgAdNbo26G9Xxcvn6IG5hYxFYzUvVrd8j1zdTECQCPkzBZ8"
                    + "6cYgWPxbnbsFb92lwiW7tswjvlzDJET4Z1841wzvcg1yZtJ1GSV4AGGAkjXtBvgS"
                    + "93GJy0V6rNeOs0I=";

    static {
        try {
            CIPHER = Cipher.getInstance(CIPHER_ALGORITHM);
            KEY_FACTORY = KeyFactory.getInstance(KEY_ALGORITHM);

            defaultPublicKey = loadPublicKey(defaultPublicKeyStr);
            defaultPrivateKey = loadPrivateKey(defaultPrivateKeyStr);

        } catch (NoSuchAlgorithmException e) {
            log.error("RsaEncryptor init error:", e);
        } catch (NoSuchPaddingException e) {
            log.error("RsaEncryptor init error:", e);
        } catch (Exception e) {
            log.error("RsaEncryptor init error:", e);
        }
    }

    /**
     * RAS encrypt
     *
     * @param plainText
     *
     * @return
     */
    public static String encrypt(final String plainText) {
        try {
            return encryptWithBase64(plainText);
        } catch (Exception e) {
            log.error("RsaEncryptor encrypt error:", e);
        }
        return null;
    }

    /**
     * RSA decrypt
     *
     * @param cipherText
     *
     * @return
     */
    public static String decrypt(final String cipherText) {
        try {
            return decryptWithBase64(cipherText);
        } catch (Exception e) {
            log.error("RsaEncryptor decrypt error:", e);
        }
        return null;
    }

    /**
     * Randomly generated key pair
     */
    public static void genKeyPair() {
        try {
            final KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(1024, new SecureRandom());
            final KeyPair keyPair = keyPairGen.generateKeyPair();
            defaultPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
            defaultPublicKey = (RSAPublicKey) keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            log.error("RsaEncryptor genKeyPair error:", e);
        }
    }

    /**
     * Load the public key from a string.
     *
     * @param publicKeyStr
     *
     * @throws Exception
     */
    public static RSAPublicKey loadPublicKey(final String publicKeyStr) throws Exception {
        try {
            final byte[] buffer = Base64.decodeBase64(publicKeyStr);
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) KEY_FACTORY.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new Exception("invalid public key", e);
        } catch (NullPointerException e) {
            throw new Exception("public key error", e);
        }
    }

    /**
     * Load the private key from a string.
     *
     * @param privateKeyStr
     *
     * @throws Exception
     */
    public static RSAPrivateKey loadPrivateKey(final String privateKeyStr) throws Exception {
        try {
            final byte[] buffer = Base64.decodeBase64(privateKeyStr);
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            return (RSAPrivateKey) KEY_FACTORY.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new Exception("invalid private key", e);
        } catch (NullPointerException e) {
            throw new Exception("private key error", e);
        }
    }

    public static String decryptWithBase64(final String base64String) throws Exception {
        final byte[] binaryData = decrypt(defaultPrivateKey, Base64.decodeBase64(base64String));
        return new String(binaryData, StandardCharsets.UTF_8);
    }

    public static String encryptWithBase64(final String string) throws Exception {
        final byte[] binaryData =
                encrypt(defaultPublicKey, string.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(binaryData);
    }

    /**
     * javaScript encrypt =>> java decrypt
     *
     * @param str
     *
     * @return
     */
    public static String decryptFromJs(final String str) {
        try {
            final byte[] deResult = decryptJs(defaultPrivateKey, Hex.decodeHex(str.toCharArray()));
            final StringBuffer sb = new StringBuffer();
            sb.append(new String(deResult, StandardCharsets.UTF_8));
            return URLDecoder.decode(sb.reverse().toString(), StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.error("decryptFromJs error", e);
        }
        return null;
    }

    /**
     * javaScript encrypt =>> java decrypt
     *
     * @param pk  private key
     * @param str str
     *
     * @return
     */
    public static String decryptFromJs(final PrivateKey pk, final String str) {
        try {
            final byte[] deResult = decryptJs(pk, Hex.decodeHex(str.toCharArray()));
            final StringBuffer sb = new StringBuffer();
            sb.append(new String(deResult, StandardCharsets.UTF_8));
            return URLDecoder.decode(sb.reverse().toString(), StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.error("decryptFromJs error", e);
        }
        return null;
    }

    /**
     * javaScript encrypt =>> java decrypt
     *
     * @param pk  private key
     * @param raw
     *
     * @return
     */
    public static byte[] decryptJs(final PrivateKey pk, final byte[] raw) throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(64)) {
            final Cipher cipher = Cipher.getInstance(KEY_ALGORITHM, DEFAULT_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, pk);
            final int blockSize = cipher.getBlockSize();
            int j = 0;
            while (raw.length - j * blockSize > 0) {
                bos.write(cipher.doFinal(raw, j * blockSize, blockSize));
                j++;
            }
            return bos.toByteArray();
        }
    }

    /**
     * encrypt
     *
     * @param publicKey
     * @param plainTextData
     *
     * @return
     */
    public static byte[] encrypt(final RSAPublicKey publicKey, final byte[] plainTextData)
            throws Exception {
        try {
            if (publicKey == null) {
                throw new Exception("Encryption public key is empty");
            }
            CIPHER.init(Cipher.ENCRYPT_MODE, publicKey);
            return CIPHER.doFinal(plainTextData);
        } catch (InvalidKeyException e) {
            throw new Exception("Illegal encryption key", e);
        } catch (IllegalBlockSizeException e) {
            throw new Exception("Illegal plaintext length", e);
        } catch (BadPaddingException e) {
            throw new Exception("The plaintext data is corrupted", e);
        }
    }

    /**
     * decrypt
     *
     * @param privateKey
     * @param cipherData
     *
     * @return
     */
    public static byte[] decrypt(final RSAPrivateKey privateKey, final byte[] cipherData)
            throws Exception {
        try {
            if (privateKey == null) {
                throw new Exception("Decryption private key is empty");
            }
            CIPHER.init(Cipher.DECRYPT_MODE, privateKey);
            return CIPHER.doFinal(cipherData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("No such decryption algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new Exception("Decryption private key is illegal", e);
        } catch (InvalidKeyException e) {
            throw new Exception("Decryption private key is illegal", e);
        } catch (IllegalBlockSizeException e) {
            throw new Exception("Illegal ciphertext length", e);
        } catch (BadPaddingException e) {
            throw new Exception("The ciphertext data is corrupted", e);
        }
    }

    /**
     * MD5_WITH_RSA
     *
     * @param value
     *
     * @return
     */
    public static String sign(final String value, final SignAlgorithm algorithm) {
        try {
            if (StringUtils.isBlank(value)) {
                throw new Exception("Signature value cannot be empty");
            }

            final Signature signet = Signature.getInstance(algorithm.getAlgorithm());
            signet.initSign(defaultPrivateKey);
            signet.update(value.getBytes(StandardCharsets.UTF_8));
            final byte[] signed = signet.sign();
            return new String(Base64.encodeBase64(signed), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("RsaEncryptor sign error:", e);
        }
        return null;
    }

    /**
     * MD5_WITH_RSA
     *
     * @param value
     * @param sign
     *
     * @return
     */
    public static boolean verify(final String value, final String sign,
                                 final SignAlgorithm algorithm) {
        try {
            if (StringUtils.isBlank(value)) {
                throw new Exception("Signature value cannot be empty");
            }

            final byte[] signed = Base64.decodeBase64(sign);
            final Signature signature = Signature.getInstance(algorithm.getAlgorithm());
            signature.initVerify(defaultPublicKey);
            signature.update(value.getBytes(StandardCharsets.UTF_8));

            return signature.verify(signed);
        } catch (Exception e) {
            log.error("RsaEncryptor verify error:", e);
        }

        return false;
    }

    /**
     * MD5_WITH_RSA
     *
     * @param value
     *
     * @return
     */
    public static String signMd5WithRsa(final String value) {
        return sign(value, SignAlgorithm.MD5_WITH_RSA);
    }

    /**
     * MD5_WITH_RSA
     *
     * @param value
     * @param sign
     *
     * @return
     */
    public static boolean verifyMd5WithRsa(final String value, final String sign) {
        return verify(value, sign, SignAlgorithm.MD5_WITH_RSA);
    }

    /**
     * SHA1_WITH_RSA
     *
     * @param value
     *
     * @return
     */
    public static String signSha1WithRsa(final String value) {
        return sign(value, SignAlgorithm.SHA1_WITH_RSA);
    }

    /**
     * SHA1_WITH_RSA
     *
     * @param value
     * @param sign
     *
     * @return
     */
    public static boolean verifySha1WithRsa(final String value, final String sign) {
        return verify(value, sign, SignAlgorithm.SHA1_WITH_RSA);
    }

    public static void main(String[] args) throws Exception {
        try {
            System.out.println("================== RSA encrypt ========================");
            String plainText = "1234567890";
            String cipherText = RsaEncryptor.encrypt(plainText);
            System.out.println("cipherText:" + cipherText);

            String result = RsaEncryptor.decrypt(cipherText);
            System.out.println("plainText:" + plainText);
            System.out.println("cipherText:" + cipherText);
            System.out.println("result:" + result);

            System.out.println("================== RSA verifyMd5WithRsa ========================");
            String data = "hello-world";
            String sign = RsaEncryptor.signMd5WithRsa(data);
            System.out.println("====>>sign:" + sign);
            System.out.println("====>>result:" + RsaEncryptor.verifyMd5WithRsa(data, sign));

            System.out.println("================== RSA verifySha1WithRsa ========================");
            String data2 = "hello-world";
            String sign2 = RsaEncryptor.signSha1WithRsa(data2);
            System.out.println("====>>sign2:" + sign2);
            System.out.println("====>>result2:" + RsaEncryptor.verifySha1WithRsa(data2, sign2));

        } catch (Exception e) {
            log.error("RsaEncryptor.main error:", e);
        }
    }

    public enum SignAlgorithm {

        MD5_WITH_RSA("MD5WithRSA"), SHA1_WITH_RSA("SHA1WithRSA");

        private String algorithm;

        SignAlgorithm(final String algorithm) {
            this.algorithm = algorithm;
        }

        public static SignAlgorithm parse(final String algorithm) {
            return Arrays.stream(SignAlgorithm.values())
                    .filter(val -> val.getAlgorithm().equals(algorithm)).findFirst().orElse(null);
        }

        public String getAlgorithm() {
            return algorithm;
        }
    }
}
