CREATE TABLE `account_balance_snapshot`
(
    `id`                     bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'FK accountId',
    `merchant_id`            bigint       NOT NULL COMMENT 'FK',
    `merchant_code`          varchar(32)  NOT NULL COMMENT 'Merchant code',
    `merchant_name`          varchar(256) NOT NULL COMMENT 'Merchant name',
    `country_code`           varchar(2)   NOT NULL COMMENT 'Country Code',
    `transaction_type_code`  varchar(32)  NOT NULL COMMENT 'TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS',
    `latest_daily_balance`   decimal(26, 0) NULL DEFAULT '0' COMMENT 'Yesterday`s balance',
    `sub_total_amount`       decimal(26, 0) NULL DEFAULT '0' COMMENT 'current daily increment amount',
    `total_balance`          decimal(26, 0) NULL DEFAULT '0' COMMENT 'total_balance = latest_daily_balance+sub_total_amount',
    `extractable_balance`    decimal(26, 0) NULL DEFAULT '0' COMMENT 'extractable balance',

    `account_config`         json NULL COMMENT 'account config',
    `exchange_rate_currency` json NULL COMMENT 'exchange rate currency',

    `holding_limit`          decimal(26, 0) NULL DEFAULT '0' COMMENT 'Monthly limit threshold, unit:cent; Amount to holding when the transaction triggers the threshold',
    `holding_amount`         decimal(26, 0) NULL DEFAULT '0' COMMENT 'holding amount',

    `available_amount`       decimal(26, 0) NULL DEFAULT '0' COMMENT 'available amount',
    `unavailable_amount`     decimal(26, 0) NULL DEFAULT '0' COMMENT 'unavailable amount',
    `pending_amount`         decimal(26, 0) NULL DEFAULT '0' COMMENT 'pending amount',

    `currency`               varchar(3)   NOT NULL COMMENT 'balance pool currency properties',
    `timezone`               varchar(8)   NOT NULL COMMENT 'local time zone',
    `timezone_name`          varchar(64) NULL DEFAULT '' COMMENT 'timezone name',

    `created_time`           datetime     NOT NULL COMMENT 'created time',
    `updated_time`           datetime     NOT NULL COMMENT 'updated time',
    `created_by`             bigint NULL DEFAULT '0',
    `updated_by`             bigint NULL DEFAULT '0',
    `version`                bigint unsigned NULL DEFAULT '1' COMMENT 'optimistic locking',
    `del_flag`               tinyint(1) NULL DEFAULT '0' COMMENT '0-normal，1-delete',
    `remark`                 varchar(200) NULL DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                      `udx_merchant_country_account` (`merchant_id`,`country_code`,`transaction_type_code`) USING BTREE,
    KEY                      `idx_merchant_account` (`merchant_id`,`transaction_type_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='account balance snapshot';

