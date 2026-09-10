-- prod, dev has been execute;
ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    DROP INDEX `idx_hold_status`,
    ADD INDEX `idx_hold_status`(`country_code`, `transaction_type_code`, `merchant_code`, `hold_status`, `document_id`) USING BTREE;