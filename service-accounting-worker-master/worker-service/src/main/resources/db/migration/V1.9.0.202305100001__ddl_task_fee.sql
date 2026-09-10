ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD COLUMN `final_status_timestamp` int NOT NULL DEFAULT 0 COMMENT 'timestamp of final status' AFTER `event_timestamp`,
    ADD INDEX `idx_transction_report`(`merchant_code` ASC, `country_code` ASC, `transaction_type_code` ASC, `final_status_timestamp` DESC) USING BTREE;