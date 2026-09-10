package com.liquido.core.common.feign;

import feign.Retryer;
import org.springframework.context.annotation.Bean;


/**
 * add @configuration annotation is a global reference
 * not need global reference, just add @FeignClient configuration=CustomerRetry.class
 */
public class CustomerRetryConfig {
    @Bean
    Retryer getRetryBean() {
        return new CustomerRetry();
    }
}
