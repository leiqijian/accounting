ALTER TABLE `service_accounting_statement`.`transaction_fee`
    ADD INDEX `idx_account_transaction_settle_bill`(`account_id`, `transaction_time`, `instant_flag`, `settle_status`, `bill_id`, `del_flag`) USING BTREE;

ALTER TABLE `service_accounting_statement`.`transaction_money`
    ADD INDEX `idx_account_transaction_settle_bill`(`account_id`, `transaction_time`, `settle_status`, `bill_id`, `del_flag`) USING BTREE;

