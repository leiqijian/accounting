ALTER TABLE `service_accounting_statement`.`account_statement`
    ADD INDEX `idx_transaction_time`(`transaction_time`) USING BTREE;