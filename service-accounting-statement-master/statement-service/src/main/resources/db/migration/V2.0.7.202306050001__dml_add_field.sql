ALTER TABLE `service_accounting_statement`.`transaction_cost`
    ADD COLUMN `bill_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT 'bill Id' AFTER `unique_id`,
    ADD INDEX `idx_account_bill` (`account_id`, `bill_id`) USING BTREE;