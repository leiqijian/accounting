package com.liquido.statement.common.cache;


public class CacheConstant {
    public static final String HOME_CALENDAR = "HOME_CALENDAR:%s:%s";

    public static final String ACCOUNT_INFO_HASH = "ACCOUNT_INFO";

    public static final String TRANSACTION_ORDER = "TRANSACTION_ORDER:";

    public static final String GLOBAL_ACCOUNT_LOCK_KEY = "GLOBAL_ACCOUNT_LOCK:";

    public static final String ACCOUNT_LOCK_KEY = "ACCOUNT_LOCK:";

    public static final String ACCOUNT_IN_PROGRESS_LOCK_KEY = "ACCOUNT_PROGRESS_LOCK:";

    public static final String ACCOUNT_DIAGNOSE_KEY = "ACCOUNT_DIAGNOSE";

    public static final String DAILY_CUT_LOCK = "DAILY_CUT_LOCK:%s";

    public static final String ACCOUNT_DAILY_INIT_LOCK = "ACCOUNT_DAILY_INIT_LOCK:%s";

    public static final String ACCOUNT_DAILY_INIT = "ACCOUNT_DAILY_INIT:%s";

    public static final String COMPLETED_DAILY_CUT = "COMPLETED_DAILY_CUT";

    public static final String PAYMENT_TOKEN_KEY = "PAYMENT_TOKEN";

    public static final String PAYMENT_CONFIG_HASH = "PAYMENT_CONFIG";

    public static final String BILL_RECALCULATION_SWITCH = "BILL_RECALCULATION_SWITCH";

    public static final String ACCOUNT_DAILY_BILL_KEY = "ACCOUNT_DAILY_BILL:%s";

    public static final String ACCOUNT_HOLDING_KEY = "ACCOUNT_HOLDING:";

    public static final String HOURLY_EXCHANGE_RATE = "HOURLY_EXCHANGE_RATE:%s_%s_%s";

    public static final String AUTO_TRANSFER_OUT_KEY = "AUTO_TRANSFER_OUT:%s:%s";

    public static final String PAYMENT_PAYOUT_LOCK = "PAYMENT_PAYOUT_LOCK:";

    public static final String ACCOUNT_WITHDRAWAL_APPLY_LOCK = "WITHDRAWAL_APPLY_LOCK:";

    public static final String ACCOUNT_BALANCE_ALARM_KEY = "ACCOUNT_BALANCE_ALARM:%s";

    public static final String ACCOUNT_WITHDRAWAL_BALANCE_OVER_THRESHOLD_ALARM =
            "ACCOUNT_WITHDRAWAL_BALANCE_OVER_THRESHOLD_ALARM:%s";

    public static final String TRANSACTION_SUMMARY_INIT_LOCK
            = "TRANSACTION_SUMMARY_INIT_LOCK:%s:%s";

    public static final String LARK_MONITOR_INSUFFICIENT_BALANCE
            = "LARK_MONITOR_INSUFFICIENT_BALANCE";

    public static final String ACCOUNT_BALANCE_SNAPSHOT = "ACC_BALANCE_SNAPSHOT:%s";

    public static String buildDailyCutLockKey(final Long accountId) {
        return String.format(CacheConstant.DAILY_CUT_LOCK, accountId);
    }

}
