ALTER TABLE `service_accounting_base`.`credit_card_group`
    CHANGE COLUMN `inn_begin` `iin_begin` int NULL DEFAULT 0 COMMENT 'iin range begin' AFTER `group_code`,
    CHANGE COLUMN `inn_end` `iin_end` int NULL DEFAULT 0 COMMENT 'iin range end' AFTER `iin_begin`;