ALTER TABLE `service_accounting_statement`.`daily_exchange_rate`
    CHANGE COLUMN `exchange_date` `exchange_time` datetime NOT NULL COMMENT 'exchange to datetime(UTC0) format:yyyy-MM-dd HH:mm:ss' AFTER `target_currency`,
DROP INDEX `udx_unique_key`,
    ADD UNIQUE INDEX `udx_exchange_rate`(`merchant_id`, `account_id`, `exchange_time`, `source_currency`, `target_currency`) USING BTREE;