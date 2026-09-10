ALTER TABLE `service_accounting_statement`.`transaction_money`
    ADD INDEX `idx_account_hold`(`account_id`, `hold_status`) USING BTREE;