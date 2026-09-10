ALTER TABLE service_accounting_statement.transaction_money
    ADD COLUMN unique_id varchar(64) NOT NULL COMMENT 'from global transaction system uniqueId, reference task_fee_calculation.unique_id' AFTER transaction_id,
    ADD INDEX idx_unique_id(unique_id) USING BTREE;

ALTER TABLE service_accounting_statement.transaction_fee
    ADD COLUMN unique_id varchar(64) NOT NULL COMMENT 'from global transaction system uniqueId, reference task_fee_calculation.unique_id' AFTER transaction_id,
	ADD INDEX idx_unique_id(unique_id) USING BTREE;