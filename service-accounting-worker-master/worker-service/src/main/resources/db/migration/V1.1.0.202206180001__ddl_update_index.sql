ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
DROP INDEX `task_fee_calculation_event_time_index`,
ADD INDEX `task_fee_calculation_transaction_time_index`(`transaction_time`) USING BTREE;