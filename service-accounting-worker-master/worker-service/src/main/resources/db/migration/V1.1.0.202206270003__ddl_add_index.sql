ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD INDEX `idx_transaction_query` (`transaction_timestamp`)
    USING BTREE;

ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    MODIFY COLUMN `transaction_timestamp` int NOT NULL AFTER `transaction_time`;