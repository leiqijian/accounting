ALTER TABLE service_accounting_worker.task_log_fee_calculation
ADD COLUMN request_id varchar(64) NOT NULL COMMENT 'request_id' AFTER id;