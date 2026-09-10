CREATE TABLE `service_accounting_statement`.`transaction_summary`
(
    `id`                   bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `account_id`           bigint unsigned NOT NULL COMMENT 'FK ref account.id',
    `transaction_date`     date            NOT NULL COMMENT 'transaction date',
    `transaction_amount`   decimal(26, 0) DEFAULT '0' COMMENT 'daily total  transaction amount(unit:cent)',
    `transaction_count`    int unsigned   DEFAULT '0' COMMENT 'daily total  transaction count',
    `transaction_currency` varchar(3)     DEFAULT NULL COMMENT 'transaction currency',
    `exchange_rate`        decimal(26, 6) DEFAULT NULL COMMENT 'daily exchange rate',
    `settlement_amount`    decimal(26, 0) DEFAULT '0' COMMENT 'daily total settlement amount(excluded fee), formula: transaction_amount * exchange_rate, unit:cent',
    `fee_amount`           decimal(26, 0) DEFAULT '0' COMMENT 'daily total settlement fee amount(unit:cent)',
    `tax_amount`           decimal(26, 0) DEFAULT '0' COMMENT 'daily total settlement tax amount(unit:cent)',
    `settlement_currency`  varchar(3)     DEFAULT NULL COMMENT 'settlement currency',
    `remark`               varchar(200)   DEFAULT '' COMMENT 'remark',
    `version`              int unsigned   DEFAULT '0' COMMENT 'version (optimistic locking)',
    `created_time`         datetime       DEFAULT NULL COMMENT 'created time (utc0)',
    `updated_time`         datetime       DEFAULT NULL COMMENT 'updated time (utc0)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_account_transaction` (`account_id`, `transaction_date`) USING BTREE,
    KEY `idx_transaction_date` (`transaction_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;