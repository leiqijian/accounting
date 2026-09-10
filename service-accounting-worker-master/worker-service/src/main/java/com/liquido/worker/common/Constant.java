package com.liquido.worker.common;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "checkstyle:TypeName"})
public interface Constant {

    DateTimeFormatter GROUP_BY_HOUR = DateTimeFormatter.ofPattern("yyyyMMddHH");

    BigDecimal TWO = new BigDecimal("2");

    interface COMMON {

        ZoneId ZONE_UTC = ZoneId.of("UTC");

        Integer BATCH_SYNC_SIZE = 5000;
    }

    interface CALCULATION_RULE {

        String DEFAULT_RULE = "DEFAULT";

        // CREDIT CARD INSTALLMENT, $CardBrand:INSTALLMENT:$Periods
        String INSTALLMENT_RULE = "%s:INSTALLMENT:%s";

        String CARD_GROUP_TYPE = "cardGroupType";

        String INSTALLMENTS = "Installments";

        String CARD_TYPE = "cardType";

        String CARD_REGION = "cardGroupType";

        String CARD_USE_3DS = "cardUse3ds";

    }

    interface CACHE {

        String EXCHANGE_RATE_AUTH_TOKEN = "EXCHANGE_RATE_AUTH_TOKEN";

        String EXCHANGE_RATE_HASH = "EXCHANGE_RATE";
        int EXCHANGE_RATE_EXPIRE_DAY = 3;

        String MERCHANT_INFO_HASH = "MERCHANT_INFO";

        String MERCHANT_ACCOUNT_HASH = "MERCHANT_ACCOUNT";

        String MONTHLY_FEE_CONFIG_HASH = "MONTHLY_FEE_CONFIG:%s";

        String COST_CONFIG_APM_LIST = "COST_CONFIG_APM_LIST";

        String COST_CONFIG_CARD_LIST = "COST_CONFIG_CARD_LIST";

        String WORKDAY_HASH = "WORKDAY:%s";

        String ACCOUNT_PRODUCT_CONFIG = "ACCOUNT_PRODUCT_CONFIG";

        String MERCHANT_ACCOUNT_INFO = "MERCHANT_ACCOUNT_INFO";

        String FEE_CALCULATION_TIMER_LOCK = "FEE_CALCULATION_TIMER_LOCK";

        String TRANSACTION_METRIC_LOCK = "TRANSACTION_METRIC_LOCK";

        String TASK_HOLDING_MONITOR = "TASK_HOLDING_MONITOR:%s:%s";

        String CREDIT_CARD_GROUP = "CREDIT_CARD_GROUP";

        String LARK_MONITOR_DONT_HAVE_ACCOUNT = "LARK_MONITOR_DONT_HAVE_ACCOUNT:%s:%s:%s";

        String LARK_MONITOR_DONT_HAVE_PRODUCT = "LARK_MONITOR_DONT_HAVE_PRODUCT:%s:%s:%s:%s";

        String LARK_MONITOR_DONT_HAVE_VENDOR_TYPE = "LARK_MONITOR_DONT_HAVE_VENDOR_TYPE:%s";

        String LARK_MONITOR_DONT_HAVE_VENDOR = "LARK_MONITOR_DONT_HAVE_VENDOR:%s";

        String LARK_MONITOR_SAME_UNIQUE_ID = "LARK_MONITOR_SAME_UNIQUE_ID:%s";

        String LARK_MONITOR_PAYMENT_LINK_NOT_HAVE_MERCHANT_NAME =
                "LARK_MONITOR_PAYMENT_LINK_NOT_HAVE_MERCHANT_NAME:%s";

        String LARK_MONITOR_SHOPIFY_NOT_HAVE_MERCHANT_NAME =
                "LARK_MONITOR_SHOPIFY_NOT_HAVE_MERCHANT_NAME:%s";

        String LARK_MONITOR_SHOPLAZZA_NOT_HAVE_MERCHANT_NAME =
                "LARK_MONITOR_SHOPLAZZA_NOT_HAVE_MERCHANT_NAME:%s";

        String LARK_MONITOR_TOKENIZATION_NOT_HAVE_MERCHANT_NAME =
                "LARK_MONITOR_TOKENIZATION_NOT_HAVE_MERCHANT_NAME:%s";

        String TRANSACTION_WORK_ORDER_QUERY_STATUS = "TRANSACTION_WORK_ORDER_QUERY_STATUS:%s:%s";

        String PAYMENT_LINK_SYNC_TIMER_LOCK = "PAYMENT_LINK_SYNC_TIMER_LOCK";

        String SHOPIFY_SYNC_TIMER_LOCK = "SHOPIFY_SYNC_TIMER_LOCK";

        String SHOPLAZZA_SYNC_TIMER_LOCK = "SHOPLAZZA_SYNC_TIMER_LOCK";
        String TOKENIZATION_SYNC_TIMER_LOCK = "TOKENIZATION_SYNC_TIMER_LOCK";

        String TOKENIZATION_SYNC_ERROR_COUNT = "TOKENIZATION_SYNC_ERROR_COUNT:%s";

        String TOKENIZATION_SYNC_CALCULATION_ERROR_COUNT =
                "TOKENIZATION_SYNC_CALCULATION_ERROR_COUNT:%s";

        String PAYMENT_LINK_REFUND_INFO = "PAYMENT_LINK_REFUND_INFO:%s";

        String TRANSACTION_ALREADY_REFUND = "TRANSACTION_ALREADY_REFUND:%s";

        String LARK_MONITOR_PAYMENT_LINK_NOT_FOUND_SEATTLE =
                "LARK_MONITOR_PAYMENT_LINK_NOT_FOUND_SEATTLE:%s";

        String LARK_MONITOR_SHOPIFY_NOT_FOUND_SEATTLE =
                "LARK_MONITOR_SHOPIFY_NOT_FOUND_SEATTLE:%s";

        String LARK_MONITOR_SHOPLAZZA_NOT_FOUND_SEATTLE =
                "LARK_MONITOR_SHOPLAZZA_NOT_FOUND_SEATTLE:%s";

        String DW_AUTH_TOKEN = "DW_AUTH_TOKEN:%s";

        String SUB_MERCHANT_INFO = "SUB_MERCHANT_INFO";

        String CHECK_MONTH_FEE_CONFIG_FLAG = "CHECK_MONTH_FEE_CONFIG_FLAG:%s";

    }

    interface AWS_QUEUE_KEYS {

        /**
         * queue used to sync fee calculation from date warehouse of definite time
         */
        String TIMER_FEE_CALCULATION = "timer-fee-calculation";

        /**
         * queue used to generate month config of definite time
         */
        String TIMER_FEE_MONTH_CONFIG_GENERATE = "timer-fee-month-config-generate";

        /**
         * queue used to sync transaction metric from date warehouse
         */
        String TIMER_TRANSACTION_METRIC = "timer-transaction-metric";

        /**
         * queue used to init exchange rate of daily
         */
        String TIMER_DAILY_INIT_EXCHANGE_RATE = "timer-daily-init-exchange-rate";

        /**
         * queue used to sync payment link from date warehouse of definite time
         */
        String TIMER_PAYMENT_LINK_SYNC = "timer-payment-link-sync";

        /**
         * queue used to sync shopify from date warehouse of definite time
         */
        String TIMER_SHOPIFY_SYNC = "timer-shopify-sync";

        /**
         * queue used to sync shoplazza from date warehouse of definite time
         */
        String TIMER_SHOPLAZZA_SYNC = "timer-shoplazza-sync";

        /**
         * queue used to sync tokenization from date warehouse of definite time
         */
        String TIMER_TOKENIZATION_SYNC = "timer-tokenization-sync";

        /**
         * queue used to save date warehouse data
         */
        String SERVICE_FEE_CALCULATION_SYNC = "service-fee-calculation-sync";

        /**
         * queue used to active fee calculate from date warehouse data
         */
        String SERVICE_FEE_CALCULATION = "service-fee-calculation";

        /**
         * queue used to save date warehouse data of payment link
         */
        String SERVICE_PAYMENT_LINK_SYNC = "service-payment-link-sync";

        /**
         * queue used to save date warehouse data of shopify
         */
        String SERVICE_SHOPIFY_SYNC = "service-shopify-sync";

        /**
         * queue used to save date warehouse data of shoplazza
         */
        String SERVICE_SHOPLAZZA_SYNC = "service-shoplazza-sync";

        /**
         * queue used to save date warehouse data of tokenization
         */
        String SERVICE_TOKENIZATION_SYNC = "service-tokenization-sync";

        /**
         * queue used to create refund approval
         */
        String SERVICE_CREATE_APPROVAL_BIZ_REFUND_KEY = "service-create-approval-biz-refund";

        String SERVICE_TRANSACTION_INPROGRESS_SYNC = "service-transaction-inprogress-sync";

    }

    interface CepDownload {
        String UNIPAGOS_NAME = "UNIPAGOS";
        String COOKIE_DOWNLOAD = "descarga.do?formato=PDF";
    }

}
