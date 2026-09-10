ALTER TABLE `service_accounting_statement`.`transaction_cost`
    ADD COLUMN `sub_merchant_id` varchar(64) COMMENT 'sub merchant id' AFTER `merchant_id`;

ALTER TABLE `service_accounting_statement`.`transaction_fee`
    ADD COLUMN `sub_merchant_id` varchar(64) COMMENT 'sub merchant id' AFTER `merchant_id`;

ALTER TABLE `service_accounting_statement`.`transaction_money`
    ADD COLUMN `sub_merchant_id` varchar(64) COMMENT 'sub merchant id' AFTER `merchant_id`;