ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
DROP COLUMN `event_time`,
ADD COLUMN `lifecycle_status` varchar(32) NOT NULL DEFAULT 'INITIAL_STATUS' AFTER `direction_type`,
ADD COLUMN `lifecycle_timestamp` int NOT NULL DEFAULT 0 AFTER `lifecycle_status`,
ADD COLUMN `calculation_node` json NULL AFTER `trade_transaction_type`;