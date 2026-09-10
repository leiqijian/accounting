package com.liquido.base.common;

import java.math.BigDecimal;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public interface Constant {

    interface CACHE {
        String PRODUCT_FEE_CONFIG = "PRODUCT_FEE_CONFIG";

        String MONTH_FEE_CONFIG = "MONTH_FEE_CONFIG";

    }

    /**
     * calculation rule comparison Type
     */
    interface COMPARISON {

        String EQUALS = "EQUALS";

        String INSTALLMENT_CONTAINS = "INSTALLMENT_CONTAINS";

    }

    /**
     * constant value of payment-config biz.
     */
    interface PaymentConfig {

        int DEFAULT_PRIORITY = 999;

        int DEFAULT_DELAY_EXECUTION = 0;

        BigDecimal DEFAULT_MAX_AMOUNT = BigDecimal.valueOf(100000000000L);

    }

    interface LARK {
        String ACCESS_TOKEN = "LARK_ACCESS_TOKEN";
    }
}
