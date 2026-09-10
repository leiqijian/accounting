ALTER TABLE `service_accounting_statement`.`account`
    ADD COLUMN `sub_total_count` int UNSIGNED NULL DEFAULT 0 COMMENT 'transaction count of the day' AFTER `sub_total_amount`;