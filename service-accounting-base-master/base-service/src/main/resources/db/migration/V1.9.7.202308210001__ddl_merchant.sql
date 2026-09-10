ALTER TABLE `service_accounting_base`.`merchant`
    ADD COLUMN `report_weight` int NOT NULL DEFAULT 1 COMMENT 'Merchant report weight' AFTER `weight`;