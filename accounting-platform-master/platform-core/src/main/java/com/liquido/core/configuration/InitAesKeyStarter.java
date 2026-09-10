package com.liquido.core.configuration;

import java.util.Objects;

import com.liquido.core.common.security.AesUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

@Slf4j
@Component
@DependsOn("secretsManagerClient")
public class InitAesKeyStarter implements CommandLineRunner {

    private final SecretsManagerClient secretsManagerClient;

    @Value("${aws.secretsmanager.secrets-names.aes-key:"
            + "accounting-service/secret/aes-key}")
    private String secretName;

    @Autowired
    public InitAesKeyStarter(final SecretsManagerClient secretsManagerClient) {
        this.secretsManagerClient = secretsManagerClient;
    }

    @Override
    public void run(final String... args) {
        String key;
        try {
            key = StringUtils.defaultIfBlank(secretsManagerClient.getSecretValue(
                                    GetSecretValueRequest.builder().secretId(secretName).build())
                            .secretString(),
                    System.getenv("AES_KEY"));
            log.info("get aes key success");
        } catch (Exception e) {
            log.error("get AES key error: {}", e.getMessage());
            key = System.getenv("AES_KEY");
        }

        if (Objects.isNull(key)) {
            log.error("get aes util key failed");
        }

        AesUtil.setKey(key);
    }
}
