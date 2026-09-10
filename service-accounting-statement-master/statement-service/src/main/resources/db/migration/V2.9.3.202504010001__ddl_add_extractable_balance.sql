ALTER TABLE `service_accounting_statement`.`sub_account`
    ADD COLUMN `extractable_balance` decimal(26, 0) NOT NULL DEFAULT '0' COMMENT 'extractable balance, unit:cent' AFTER `balance`;