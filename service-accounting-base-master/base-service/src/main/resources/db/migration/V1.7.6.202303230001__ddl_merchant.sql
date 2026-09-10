ALTER TABLE `service_accounting_base`.`merchant`
    ADD COLUMN `weight` int NOT NULL DEFAULT 1 COMMENT 'Merchant weight' AFTER `logo_icon`;