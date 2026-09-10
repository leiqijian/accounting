ALTER TABLE `service_accounting_base`.`merchant`
    ADD COLUMN `industry` varchar(64) NULL DEFAULT 'Unknown' COMMENT 'industry' AFTER `name`;