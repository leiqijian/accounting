ALTER TABLE `service_accounting_statement`.`biz_transfer_out`
    ADD COLUMN `payment_config_id` bigint UNSIGNED NULL DEFAULT 0 COMMENT 'FK ref payment_config.id' AFTER `account_id`;