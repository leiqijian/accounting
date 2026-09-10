ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD INDEX `idx_merchant_reference`(`merchant_reference`) USING BTREE;