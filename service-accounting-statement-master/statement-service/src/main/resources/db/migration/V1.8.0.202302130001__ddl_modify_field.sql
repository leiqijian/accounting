ALTER TABLE `service_accounting_statement`.`account`
    ADD COLUMN `exchange_amount` decimal(26, 0) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Exchange amount' AFTER `frozen_amount`;