-- 创建 transaction_in_progress 表
DROP TABLE IF EXISTS `transaction_in_progress`;
CREATE TABLE `transaction_in_progress` (
    `id` bigint unsigned NOT NULL,
    `transaction_id` bigint NOT NULL COMMENT 'fk tack_id',
    `unique_id` varchar(128) NOT NULL COMMENT 'from global transaction system uniqueId, reference task_fee_calculation.unique_id',
    `merchant_id` bigint unsigned NOT NULL COMMENT 'fk',
    `account_id` bigint unsigned NOT NULL COMMENT 'fk',
    `merchant_code` varchar(128) NOT NULL,
    `country_code` varchar(2) NOT NULL COMMENT 'CountryCodeEnum',
    `transaction_type_code` varchar(32) NOT NULL COMMENT 'TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS',
    `amount` decimal(26, 2) NOT NULL,
    `currency` varchar(3) NOT NULL,
    `status` varchar(32) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT 'TransactionProgressStatusEnum：IN_PROGRESS/COMPLETED',
    `transaction_status` varchar(32) NOT NULL COMMENT 'TransactionStatusEnum: IN_PROGRESS/SETTLED/FAILED/CHARGED_BACK/REFUNDING/REFUNDED/EXPIRED',
    `direction_type` varchar(32) NOT NULL COMMENT 'DirectionTypeEnum: SETTLED/REFUND/CHARGE_BACK',
    `lifecycle_status` varchar(32) NOT NULL,
    `lifecycle_timestamp` int NOT NULL,
    `vendor` varchar(255) COMMENT 'vendor',
    `created_time` datetime DEFAULT NULL,
    `updated_time` datetime DEFAULT NULL,
    `version` bigint unsigned NOT NULL DEFAULT '1' COMMENT 'optimistic locking',
    `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-normal，1-delete',
    `remark` varchar(200) DEFAULT NULL COMMENT 'remark',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unique_id` (`unique_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- 创建 account_in_progress 表
DROP TABLE IF EXISTS `account_in_progress`;
CREATE TABLE `account_in_progress` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `merchant_id` bigint unsigned NOT NULL COMMENT 'fk',
    `account_id` bigint unsigned NOT NULL COMMENT 'fk',
    `in_progress_amount` decimal(26, 2) NOT NULL,
    `in_progress_currency` varchar(3) NOT NULL,
    `created_time` datetime DEFAULT NULL,
    `updated_time` datetime DEFAULT NULL,
    `version` bigint unsigned NOT NULL DEFAULT '1' COMMENT 'optimistic locking',
    `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'delFlag: 0-normal, 1-delete',
    `remark` varchar(200) DEFAULT NULL COMMENT 'remark',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account_currency` (`account_id`, `in_progress_currency`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
