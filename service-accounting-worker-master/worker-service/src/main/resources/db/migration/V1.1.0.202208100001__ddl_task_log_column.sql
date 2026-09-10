ALTER TABLE service_accounting_worker.task_log_fee_calculation
    ADD COLUMN calculate_consuming int UNSIGNED NULL DEFAULT 0 COMMENT 'calculate fee time-consuming, unit:ms' AFTER end_time,
    ADD COLUMN settlement_consuming int UNSIGNED NULL DEFAULT 0 COMMENT 'settlement time-consuming, unit:ms' AFTER calculate_consuming,
    ADD COLUMN total_consuming int UNSIGNED NULL DEFAULT 0 COMMENT 'total process time-consuming, unit:ms' AFTER settlement_consuming,
    ADD COLUMN batch_count int UNSIGNED NULL DEFAULT 0 COMMENT 'batch process count' AFTER total_consuming;