ALTER TABLE `service_accounting_statement`.`daily_exchange_rate`
    DROP INDEX `idx_merchant_account_id`,
    DROP INDEX `udx_unique_key`,
    ADD UNIQUE INDEX `udx_unique_key`(`merchant_id`, `account_id`, `exchange_date`, `source_currency`, `target_currency`) USING BTREE;

ALTER TABLE `service_accounting_statement`.`transaction_payout`
    MODIFY COLUMN `transaction_status` varchar(32) NOT NULL COMMENT 'StatusEnum: WAITING/PROCESSING/SUCCESS/FAILED' AFTER `currency`,
    ADD COLUMN `delay_execute_time` datetime NOT NULL COMMENT 'delay execute time' AFTER `payment_config`,
    ADD INDEX `idx_delay_state`(`transaction_status`, `delay_execute_time`) USING BTREE;