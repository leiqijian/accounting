package com.liquido.core.common.feign;

import feign.Retryer;

public class CustomerRetry extends Retryer.Default {
    public CustomerRetry() {
        super(100, 1000, 3);
    }
}
