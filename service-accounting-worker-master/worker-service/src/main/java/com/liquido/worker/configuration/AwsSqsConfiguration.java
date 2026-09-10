package com.liquido.worker.configuration;

import java.nio.file.Path;

import com.liquido.worker.common.properties.AwsSqsProperties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsSqsConfiguration {

    @Bean
    SqsClient amazonSqs(final AwsSqsProperties properties) {
        final String sessionName = "worker-" + System.currentTimeMillis();
        final String roleArn = System.getenv("AWS_ROLE_ARN");
        final String tokenFile = System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE");

        if (StringUtils.isEmpty(roleArn) || StringUtils.isEmpty(tokenFile)) {
            return SqsClient.builder().region(Region.of(properties.getRegion())).build();
        }

        return SqsClient.builder()
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.builder()
                        .roleArn(roleArn)
                        .roleSessionName(sessionName)
                        .webIdentityTokenFile(Path.of(tokenFile))
                        .build())
                .region(Region.of(properties.getRegion()))
                .build();
    }

}
