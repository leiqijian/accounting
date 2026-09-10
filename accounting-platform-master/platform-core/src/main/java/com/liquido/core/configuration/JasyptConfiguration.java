package com.liquido.core.configuration;

import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.security.JasyptUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

/**
 * Jasypt Encryptor
 */
@Slf4j
@Configuration
@Import(SecretManagersConfiguration.class)
public class JasyptConfiguration {

    @Value("${aws.secretsmanager.secrets-names.jasypt-password:"
            + "accounting-service/secret/jasypt-salt}")
    private String secretName;

    @Order(-100)
    @Bean("jasyptStringEncryptor")
    public PooledPBEStringEncryptor jasyptEncryptor(
            final SecretsManagerClient secretsManagerClient) {

        String password;
        try {
            password = secretsManagerClient.getSecretValue(GetSecretValueRequest.builder()
                            .secretId(secretName).build())
                    .secretString();
            if (StringUtils.isNotBlank(password)) {
                log.info("get jasypt password from secrets-manager success");
            } else {
                password = System.getenv("JASYPT_PASSWORD");
                if (StringUtils.isNotBlank(password)) {
                    log.info("get jasypt password from environment variable success");
                }
            }
        } catch (Exception e) {
            log.warn("get jasypt password error:{}", e.getMessage());

            password = System.getenv("JASYPT_PASSWORD");
            log.info("retry get jasypt password from environment variable......");
            if (StringUtils.isNotBlank(password)) {
                log.info("get jasypt password from environment variable success");
            }
        }

        if (StringUtils.isBlank(password)) {
            log.error("Init Jasypt password fail");
            throw CommonExceptionCode.ENV_CONFIG_MISSING.exception("Jasypt password undefined");
        }

        final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        final SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setProviderName(JasyptUtil.PROVIDER_NAME);
        config.setAlgorithm(JasyptUtil.ALGORITHM);
        config.setPassword(password);
        config.setPoolSize(JasyptUtil.POOL_SIZE);
        config.setKeyObtentionIterations(JasyptUtil.ITERATIONS);
        config.setStringOutputType(JasyptUtil.OUTPUT_TYPE);
        config.setSaltGeneratorClassName(JasyptUtil.SALT_GENERATOR_CLASSNAME);
        config.setIvGeneratorClassName(JasyptUtil.IV_GENERATOR_CLASSNAME);
        encryptor.setConfig(config);

        return encryptor;
    }

}

