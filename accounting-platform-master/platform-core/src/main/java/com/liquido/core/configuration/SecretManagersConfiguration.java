package com.liquido.core.configuration;

import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Configuration
public class SecretManagersConfiguration {

    @Value("${aws.secretsmanager.region:ap-southeast-1}")
    private String region;

    @Bean("secretsManagerClient")
    SecretsManagerClient secretsManagerClient() {

        final String sessionName = "platform-" + System.currentTimeMillis();
        final String roleArn = System.getenv("AWS_ROLE_ARN");
        final String tokenFile = System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE");

        if (StringUtils.isEmpty(roleArn) || StringUtils.isEmpty(tokenFile)) {
            return SecretsManagerClient.builder().region(Region.of(region)).build();
        }

        return SecretsManagerClient.builder()
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.builder()
                        .roleArn(roleArn)
                        .roleSessionName(sessionName)
                        .webIdentityTokenFile(Path.of(tokenFile))
                        .build())
                .region(Region.of(region))
                .build();
    }
}
