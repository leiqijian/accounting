ALTER TABLE `service_accounting_statement`.`transaction_cost`
    ADD INDEX `idx_account_transaction_time`(`account_id`, `transaction_time`) USING BTREE;