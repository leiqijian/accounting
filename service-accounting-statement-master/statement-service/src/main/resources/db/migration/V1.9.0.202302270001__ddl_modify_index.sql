ALTER TABLE `service_accounting_statement`.`transaction_cost`
    MODIFY COLUMN `transaction_time` datetime NOT NULL COMMENT ' The original task_fee_calculation.transaction_time UTC+0' AFTER `cost_other`,
    MODIFY COLUMN `transaction_timestamp` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Transaction timestamp' AFTER `transaction_time`,
    ADD INDEX `idx_account_direction_type`(`account_id`, `direction_type`),
    DROP INDEX `idx_account_transaction_time`;
