ALTER TABLE `service_accounting_statement`.`account_statement_biz`
    ADD COLUMN `sub_merchant_id` varchar(128) NULL DEFAULT '' COMMENT 'sub merchant id' AFTER `merchant_id`;

ALTER TABLE `service_accounting_statement`.`transaction_in_progress`
    ADD COLUMN `sub_merchant_id` varchar(128) NULL DEFAULT '' COMMENT 'sub merchant id' AFTER `merchant_id`;

ALTER TABLE `service_accounting_statement`.`sub_account_daily_bill`
    ADD COLUMN `end_extractable_balance` decimal(26, 0) NULL DEFAULT '0' COMMENT 'end extractable balance' AFTER `end_balance`;