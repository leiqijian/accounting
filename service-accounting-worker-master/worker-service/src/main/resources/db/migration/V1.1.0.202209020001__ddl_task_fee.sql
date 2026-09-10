ALTER TABLE service_accounting_worker.task_fee_calculation
    ADD COLUMN trade_transfer_status varchar(32) NOT NULL COMMENT 'trade system data: transfer_status/status' AFTER direction_type,
    ADD COLUMN trade_transaction_type varchar(32) NOT NULL COMMENT 'trade system data: transaction_type' AFTER trade_transfer_status;

