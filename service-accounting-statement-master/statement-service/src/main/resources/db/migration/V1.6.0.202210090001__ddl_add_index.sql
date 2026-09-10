ALTER TABLE `service_accounting_statement`.`biz_transfer_out`
    DROP INDEX `idx_merchant_account_id`,
    ADD INDEX `idx_merchant_account_id`(`merchant_id`, `account_id`, `bill_id`) USING BTREE;