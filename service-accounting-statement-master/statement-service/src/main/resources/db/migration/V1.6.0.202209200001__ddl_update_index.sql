
-- Old KEY `idx_account_transaction_settle_bill` (`account_id`,`transaction_time`,`settle_status`,`bill_id`,`del_flag`) USING BTREE
ALTER TABLE service_accounting_statement.transaction_money
    DROP INDEX idx_account_transaction_settle_bill,
    ADD INDEX idx_account_bill(account_id, bill_id) USING BTREE;

-- Old  KEY `idx_account_transaction_settle_bill` (`account_id`,`transaction_time`,`instant_flag`,`settle_status`,`bill_id`,`del_flag`) USING BTREE
ALTER TABLE service_accounting_statement.transaction_fee
    DROP INDEX idx_account_transaction_settle_bill,
    ADD INDEX idx_account_bill(account_id, bill_id, instant_flag) USING BTREE;