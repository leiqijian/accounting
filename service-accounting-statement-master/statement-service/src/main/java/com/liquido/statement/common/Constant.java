package com.liquido.statement.common;

import java.time.ZoneId;

@SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "checkstyle:TypeName"})
public interface Constant {

    interface COMMON {

        ZoneId ZONE_UTC = ZoneId.of("UTC");

    }

    interface AWS_QUEUE_KEYS {

        /**
         * queue used to settle the fee definite time
         */
        String TIMER_FEE_SETTLE = "timer-fee-settle";

        /**
         * queue used to lark reboot monitor report
         */
        String TIMER_LARK_MONITOR = "timer-lark-monitor";

        /**
         * queue used to alert insufficient balance of payout
         */
        String TIMER_INSUFFICIENT_BALANCE_ALERT = "timer-insufficient-balance-alert";

        String TIMER_BALANCE_OVER_THRESHOLD_REMIND = "timer-balance-over-threshold-remind";

        String TIMER_CHARGE_BACK_ORDER_DEFENSE_TIMEOUT = "timer-chargeback-order-defense-timeout";

        String SERVICE_TRANSACTION_INPROGRESS_SYNC = "service-transaction-inprogress-sync";

        String TIMER_INTERNAL_ACCOUNT_AUTO_TRANSFER = "timer-internal-account-auto-transfer";
    }

    interface CACHE {

        String TOKENIZATION_SYNC_CALCULATION_ERROR_COUNT =
                "TOKENIZATION_SYNC_CALCULATION_ERROR_COUNT:%s";

        String INPROGRESS_SYNC_CALCULATION_ERROR_COUNT =
                "INPROGRESS_SYNC_CALCULATION_ERROR_COUNT:%s";

    }

}
