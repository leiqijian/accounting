-- Production environment has been executed
ALTER TABLE service_accounting_statement.account_statement
    ADD INDEX idx_account_id_transaction_timestamp(account_id, transaction_timestamp) USING BTREE;