ALTER TABLE `service_accounting_statement`.`transaction_charge_back_order`
    CHANGE COLUMN `amount` `dispute_amount` decimal (26, 0) NOT NULL DEFAULT 0 COMMENT 'transaction amount, unit: cent' AFTER `product_code`,
    CHANGE COLUMN `reason` `dispute_reason` varchar (400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'dispute_reason' AFTER `status`,
    ADD COLUMN `payment_amount` decimal (26, 0) NOT NULL COMMENT 'Original transaction amount, unit: cent' AFTER `product_code`,
    ADD COLUMN `defense_time` datetime(0) NULL COMMENT 'defense time' AFTER `chargeback_time`,
    ADD COLUMN `days_left_to_defend` int (0) NOT NULL COMMENT 'defend count down' AFTER `defense_time`,
    ADD COLUMN `defense_deadline` datetime(0) NOT NULL COMMENT 'defense deadline' AFTER `days_left_to_defend`,
    MODIFY COLUMN `status` varchar (32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'status/(chargeback、under defense、defense won、defense lost)' AFTER `chargeback_time`,
    ADD COLUMN `defense_description` varchar (400) NULL COMMENT 'defense description' AFTER `dispute_reason`,
    ADD COLUMN `defense_appendix` json NULL COMMENT 'defense appendix' AFTER `defense_description`;