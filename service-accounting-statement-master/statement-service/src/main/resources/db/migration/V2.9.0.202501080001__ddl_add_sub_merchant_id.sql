ALTER TABLE `service_accounting_statement`.`transaction_biz`
    ADD COLUMN `sub_merchant_id` varchar(128) COMMENT 'sub merchant id' AFTER `account_id`;