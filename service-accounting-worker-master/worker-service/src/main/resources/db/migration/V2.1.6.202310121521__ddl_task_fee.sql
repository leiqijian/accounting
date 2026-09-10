ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD COLUMN `sub_merchant_id` varchar(64) COMMENT 'sub merchant id' AFTER `merchant_name`;