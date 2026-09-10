ALTER TABLE `service_accounting_base`.`merchant`
    ADD COLUMN `merger_account` tinyint NOT NULL DEFAULT 0 COMMENT '0: no merger account info; 1: merger account info' AFTER `owner`;