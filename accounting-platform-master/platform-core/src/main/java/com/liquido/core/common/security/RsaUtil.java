package com.liquido.core.common.security;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;

/**
 * RSA tools
 */
@Slf4j
public class RsaUtil {

    public static final BouncyCastleProvider DEFAULT_PROVIDER = new BouncyCastleProvider();
    private static final String SIGN_ALGORITHMS = "MD5WithRSA";
    /**
     * RSA_PUBLICKEY_PATH=C:/keys/rsa_public_key.pem
     * RSA_PRIVATEKEY_PATH=C:/keys/pkcs8_private_key.pem
     * RSA_KEY_SIZE=1024
     */

    @Value("${rsa.keypair.path:}")
    private static String RSA_KEYPAIR_STORE;
    @Value("${rsa.public-key.path:'/app/keys/rsa_public_key.pem'}")
    private static String RSA_PUBLIC_KEY_STORE;
    @Value("${rsa.private-key.path:'/app/keys/pkcs8_private_key.pem'}")
    private static String RSA_PRIVATE_KEY_STORE;
    @Value("${rsa.key-size:1024}")
    private static int KEY_SIZE;

    /**
     * Generate key pair
     *
     * @return KeyPair
     * @throws Exception
     */
    public static KeyPair generateKeyPair() throws Exception {
        try {
            final KeyPairGenerator keyPairGen = KeyPairGenerator
                    .getInstance("RSA", DEFAULT_PROVIDER);
            keyPairGen.initialize(KEY_SIZE, new SecureRandom());

            final KeyPair keyPair = keyPairGen.generateKeyPair();
            saveKeyPair(keyPair);
            savePublicKey(keyPair.getPublic());
            savePrivateKey(keyPair.getPrivate());
            return keyPair;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * save key pair
     *
     * @param keyPair
     * @throws Exception
     */
    public static void saveKeyPair(final KeyPair keyPair) throws Exception {
        try (final FileOutputStream fos = new FileOutputStream(RSA_KEYPAIR_STORE);
             final ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(keyPair);
        }
    }

    /**
     * save the public key
     *
     * @param publicKey
     * @throws Exception
     */
    public static void savePublicKey(final PublicKey publicKey) throws Exception {
        try (final FileOutputStream fos = new FileOutputStream(RSA_PUBLIC_KEY_STORE);
             final ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(publicKey);
        }
    }

    /**
     * save the private key
     *
     * @param privateKey
     * @throws Exception
     */
    public static void savePrivateKey(final PrivateKey privateKey) throws Exception {
        try (final FileOutputStream fos = new FileOutputStream(RSA_PRIVATE_KEY_STORE);
             final ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(privateKey);
        }
    }

    /**
     * get key pair
     *
     * @return
     */
    public static KeyPair getKeyPair() throws Exception {
        return (KeyPair) getKey(RSA_KEYPAIR_STORE);
    }

    /**
     * get key pair
     *
     * @return
     */
    public static KeyPair getKeyPair(final String path) throws Exception {
        return (KeyPair) getKey(StringUtils.isBlank(path) ? RSA_KEYPAIR_STORE : path);
    }

    /**
     * get public key
     *
     * @return
     */
    public static RSAPublicKey getPublicKey() throws Exception {
        return (RSAPublicKey) getKey(RSA_PUBLIC_KEY_STORE);
    }

    /**
     * get public key
     *
     * @return
     */
    public static RSAPublicKey getPublicKey(final String path) throws Exception {
        return (RSAPublicKey) getKey(StringUtils.isBlank(path) ? RSA_PUBLIC_KEY_STORE : path);
    }

    /**
     * get private key
     *
     * @return
     */
    public static RSAPrivateKey getPrivateKey() throws Exception {
        return (RSAPrivateKey) getKey(RSA_PRIVATE_KEY_STORE);
    }

    /**
     * get private key
     *
     * @return
     */
    public static RSAPrivateKey getPrivateKey(final String path) throws Exception {
        return (RSAPrivateKey) getKey(StringUtils.isBlank(path) ? RSA_PRIVATE_KEY_STORE : path);
    }

    /**
     * get key
     *
     * @return
     */
    public static Object getKey(final String path) throws Exception {
        try (final FileInputStream fis = new FileInputStream(path);
             final ObjectInputStream ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        }
    }

    /**
     * generate public key
     *
     * @param modulus
     * @param publicExponent
     * @return
     */
    public static RSAPublicKey generateRsaPublicKey(final byte[] modulus,
                                                    final byte[] publicExponent) throws Exception {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA", DEFAULT_PROVIDER);
            final RSAPublicKeySpec publicKeySpec =
                    new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
            return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception(ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    /**
     * generate private key
     *
     * @param modulus
     * @param privateExponent
     * @return
     */
    public static RSAPrivateKey generateRsaPrivateKey(final byte[] modulus,
                                                      final byte[] privateExponent)
            throws Exception {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA", DEFAULT_PROVIDER);
            final RSAPrivateKeySpec privateKeySpec =
                    new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));
            return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception(ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    /**
     * RSA encrypt
     *
     * @param pk   public key
     * @param data
     * @return
     */
    public static byte[] encrypt(final PublicKey pk, final byte[] data) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA", DEFAULT_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, pk);
        final int blockSize = cipher.getBlockSize();
        // Get the encrypted block size, such as: the data before encryption is 128 bytes,
        // and key_size=1024
        // The encrypted block size is 127 bytes, and the encrypted block is 128 bytes;
        // so there are 2 encrypted blocks, the first one is 127 bytes, and the second one is 1 byte
        final int outputSize = cipher.getOutputSize(data.length);
        final int leavedSize = data.length % blockSize;
        final int blocksSize =
                leavedSize != 0 ? data.length / blockSize + 1 : data.length / blockSize;
        final byte[] raw = new byte[outputSize * blocksSize];
        int i = 0;
        while (data.length - i * blockSize > 0) {
            if (data.length - i * blockSize > blockSize) {
                cipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize);
            } else {
                cipher.doFinal(data, i * blockSize, data.length - i * blockSize, raw,
                        i * outputSize);
                i++;
            }
        }
        return raw;
    }

    /**
     * RSA encrypt
     *
     * @param data
     * @return
     */
    public static String encrypt(final String data) throws Exception {
        return Base64.encodeBase64String(
                encrypt(getPublicKey(), data.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * RSA decrypt
     *
     * @param pk  private key
     * @param raw
     * @return
     */
    public static byte[] decrypt(final PrivateKey pk, final byte[] raw) throws Exception {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream(64);) {
            final Cipher cipher = Cipher.getInstance("RSA", DEFAULT_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, pk);
            final int blockSize = cipher.getBlockSize();
            int j = 0;
            while (raw.length - j * blockSize > 0) {
                bos.write(cipher.doFinal(raw, j * blockSize, blockSize));
                j++;
            }
            return bos.toByteArray();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * RSA decrypt
     *
     * @param data
     * @return
     */
    public static String decrypt(final String data) throws Exception {
        try {
            final byte[] enResult = Base64.decodeBase64(data);
            final byte[] deResult = decrypt(getPrivateKey(), enResult);
            final StringBuffer sb = new StringBuffer();
            sb.append(new String(deResult, StandardCharsets.UTF_8));
            return URLDecoder.decode(sb.toString(), StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * RSA decrypt
     *
     * @param data
     * @return
     */
    public static String decrypt(final RSAPrivateKey privateKey,
                                 final String data) throws Exception {
        try {
            final byte[] deResult = decrypt(privateKey, Base64.decodeBase64(data));
            final StringBuffer sb = new StringBuffer();
            sb.append(new String(deResult, StandardCharsets.UTF_8));
            return URLDecoder.decode(sb.toString(), StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * RSA sign
     *
     * @param value
     * @return
     */
    public static String sign(final String value) {
        try {
            final Signature signet = Signature.getInstance(SIGN_ALGORITHMS);
            signet.initSign(getPrivateKey());
            signet.update(value.getBytes(StandardCharsets.UTF_8));
            final byte[] signed = signet.sign();
            return new String(Base64.encodeBase64(signed), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("RsaUtil sign error:", e);
        }
        return null;
    }

    /**
     * RSA check sign
     *
     * @param value
     * @param sign
     * @return
     */
    public static boolean checkSign(final String value, final String sign) {
        try {
            final PublicKey pubKey = getPublicKey();
            final byte[] signed = Base64.decodeBase64(sign);
            final Signature signCheck = Signature.getInstance(SIGN_ALGORITHMS);
            signCheck.initVerify(pubKey);
            signCheck.update(value.getBytes(StandardCharsets.UTF_8));
            return signCheck.verify(signed);
        } catch (Exception e) {
            log.error("RsaUtil checkSign error:", e);
        }
        return false;
    }

    /**
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.trim().equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * @param args *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // generateKeyPair();
        System.out.println("===============RSA encrypt===========================");
        String test = "abcd";
        byte[] enTest = encrypt(getPublicKey(), test.getBytes(StandardCharsets.UTF_8));
        System.out.println(Base64.encodeBase64String(enTest));
        byte[] deTest = decrypt(getPrivateKey(), enTest);
        System.out.println(new String(deTest, StandardCharsets.UTF_8));

        System.out.println("===============RSA encrypt===========================");
        String tt = encrypt(test);
        System.out.println(tt);
        tt = decrypt(tt);
        System.out.println(tt);

        System.out.println("================== RSA sign ========================");
        String data = "test123456";
        String sign = sign(data);
        System.out.println("====>>sign:" + sign);
        System.out.println("====>>result:" + checkSign(data, sign));

    }
}
