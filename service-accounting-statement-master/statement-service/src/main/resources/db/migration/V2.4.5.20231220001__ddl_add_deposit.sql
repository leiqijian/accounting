CREATE TABLE `service_accounting_statement`.`merchant_deposit`
(
    `id`            bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `merchant_code` varchar(128) NOT NULL COMMENT 'merchant code',
    `merchant_id`   bigint unsigned NOT NULL COMMENT 'merchant id',
    `amount`        decimal(26, 0) unsigned NOT NULL DEFAULT '0' COMMENT 'merchant deposit amount, unit:cent',
    `currency`      varchar(3)   NOT NULL DEFAULT '' COMMENT 'currency: USD, MXN, BRL',
    `created_time`  datetime     NOT NULL COMMENT 'created time',
    `updated_time`  datetime     NOT NULL COMMENT 'updated time',
    `version`       bigint unsigned NOT NULL DEFAULT '1' COMMENT 'optimistic locking',
    `del_flag`      tinyint unsigned NOT NULL DEFAULT '0' COMMENT 'delete state: 0-normal,1-delete',
    `remark`        varchar(200)          DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_merchant_id` (`merchant_id`),
    KEY             `idx_merchant_code` (`merchant_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='merchant deposit';