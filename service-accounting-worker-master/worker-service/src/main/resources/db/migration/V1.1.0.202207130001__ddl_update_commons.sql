ALTER TABLE service_accounting_worker.task_fee_calculation
    MODIFY COLUMN transaction_time timestamp NULL DEFAULT NULL COMMENT 'transaction system final_status_time (UTC 0)' AFTER merchant_reference,
    MODIFY COLUMN transaction_timestamp int NOT NULL COMMENT 'transaction system final_status_timestamp (second)' AFTER transaction_time,
    MODIFY COLUMN submit_timestamp int NOT NULL COMMENT 'transaction system submit order time (second)' AFTER transaction_timestamp;