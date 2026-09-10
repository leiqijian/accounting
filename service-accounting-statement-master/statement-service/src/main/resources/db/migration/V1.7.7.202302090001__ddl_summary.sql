ALTER TABLE `service_accounting_statement`.`transaction_summary`
    ADD COLUMN `merchant_id` bigint NOT NULL COMMENT 'FK ref merchant.id' AFTER `id`;