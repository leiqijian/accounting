package com.liquido.core.common.security;


import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptUtil {

    public static final String POOL_SIZE = "1";
    public static final String ITERATIONS = "10000";
    public static final String OUTPUT_TYPE = "base64";
    public static final String PROVIDER_NAME = "SunJCE";
    public static final String PBE_WITH_MD5_AND_DES = "PBEWithMD5AndDES";
    public static final String ALGORITHM = "PBEWITHHMACSHA512ANDAES_256";
    public static final String IV_GENERATOR_CLASSNAME = "org.jasypt.iv.RandomIvGenerator";
    public static final String SALT_GENERATOR_CLASSNAME = "org.jasypt.salt.RandomSaltGenerator";

    /**
     * jasypt encrypt with PBEWithMD5AndDES
     *
     * @param data
     * @param salt
     *
     * @return
     */
    public static String encryptWithMD5(final String data, final String salt) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        final EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setProviderName(PROVIDER_NAME);
        config.setAlgorithm(PBE_WITH_MD5_AND_DES);
        config.setSaltGeneratorClassName(SALT_GENERATOR_CLASSNAME);
        config.setIvGeneratorClassName(IV_GENERATOR_CLASSNAME);
        config.setKeyObtentionIterations(ITERATIONS);
        config.setStringOutputType(OUTPUT_TYPE);
        config.setPassword(salt);
        config.setPoolSize(POOL_SIZE);
        encryptor.setConfig(config);

        return encryptor.encrypt(data);
    }


    /**
     * jasypt decrypt with PBEWithMD5AndDES
     *
     * @param encryptedData
     * @param salt
     *
     * @return
     */
    public static String decryptWithMD5(final String encryptedData, final String salt) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        final EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setProviderName(PROVIDER_NAME);
        config.setAlgorithm(PBE_WITH_MD5_AND_DES);
        config.setSaltGeneratorClassName(SALT_GENERATOR_CLASSNAME);
        config.setIvGeneratorClassName(IV_GENERATOR_CLASSNAME);
        config.setKeyObtentionIterations(ITERATIONS);
        config.setStringOutputType(OUTPUT_TYPE);
        config.setPoolSize(POOL_SIZE);
        config.setPassword(salt);
        encryptor.setConfig(config);

        return encryptor.decrypt(encryptedData);
    }


    /**
     * jasypt encrypt with PBEWITHHMACSHA512ANDAES_256
     *
     * @param data
     * @param salt
     *
     * @return
     */
    public static String encryptWithSHA512(final String data, final String salt) {
        final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        final SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setProviderName(PROVIDER_NAME);
        config.setAlgorithm(ALGORITHM);
        config.setSaltGeneratorClassName(SALT_GENERATOR_CLASSNAME);
        config.setIvGeneratorClassName(IV_GENERATOR_CLASSNAME);
        config.setKeyObtentionIterations(ITERATIONS);
        config.setStringOutputType(OUTPUT_TYPE);
        config.setPoolSize(POOL_SIZE);
        config.setPassword(salt);
        encryptor.setConfig(config);

        return encryptor.encrypt(data);
    }

    /**
     * jasypt decrypt with PBEWITHHMACSHA512ANDAES_256
     *
     * @param encryptedData
     * @param salt
     *
     * @return
     */
    public static String decryptWithSHA512(final String encryptedData, final String salt) {
        final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        final SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setProviderName(PROVIDER_NAME);
        config.setAlgorithm(ALGORITHM);
        config.setSaltGeneratorClassName(SALT_GENERATOR_CLASSNAME);
        config.setIvGeneratorClassName(IV_GENERATOR_CLASSNAME);
        config.setKeyObtentionIterations(ITERATIONS);
        config.setStringOutputType(OUTPUT_TYPE);
        config.setPoolSize(POOL_SIZE);
        config.setPassword(salt);
        encryptor.setConfig(config);

        return encryptor.decrypt(encryptedData);
    }

}
