/* supplemental admission */
ALTER TABLE `service_accounting_statement`.`account_statement`
    ADD COLUMN `bill_id` bigint NOT NULL DEFAULT '0' COMMENT 'bill id' AFTER `account_id`;