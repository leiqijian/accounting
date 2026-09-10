ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD COLUMN `sub_product_code`  varchar(32)  NOT NULL DEFAULT '' COMMENT 'SubProductCode' AFTER `product_code`;