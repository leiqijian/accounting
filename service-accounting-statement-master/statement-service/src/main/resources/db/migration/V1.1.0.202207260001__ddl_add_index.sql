ALTER TABLE service_accounting_statement.transaction_money
DROP INDEX idx_unique_id,
ADD UNIQUE INDEX udx_unique_id(unique_id) USING BTREE;