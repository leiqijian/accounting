ALTER TABLE `service_accounting_statement`.`transaction_money`
    MODIFY COLUMN `unique_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'from global transaction system uniqueId, reference task_fee_calculation.unique_id' AFTER `transaction_id`;

ALTER TABLE `service_accounting_statement`.`transaction_cost`
    MODIFY COLUMN `unique_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'from global transaction system uniqueId, reference task_fee_calculation.unique_id' AFTER `transaction_id`;

ALTER TABLE `service_accounting_statement`.`transaction_fee`
    MODIFY COLUMN `unique_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'from global transaction system uniqueId, reference task_fee_calculation.unique_id' AFTER `transaction_id`;