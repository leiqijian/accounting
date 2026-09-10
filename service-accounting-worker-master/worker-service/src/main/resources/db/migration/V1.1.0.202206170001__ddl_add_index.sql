ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD INDEX `idx_merchant_country_transaction`(`merchant_code`, `country_code`, `transaction_type_code`) USING BTREE;