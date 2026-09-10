ALTER TABLE `service_accounting_base`.`merchant`
    ADD COLUMN `inner_flag` tinyint NOT NULL DEFAULT 0 COMMENT 'is inner merchant' AFTER `weight`;