ALTER TABLE `task_log_fee_calculation`
    MODIFY COLUMN `execution_log` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL AFTER `task_result`;