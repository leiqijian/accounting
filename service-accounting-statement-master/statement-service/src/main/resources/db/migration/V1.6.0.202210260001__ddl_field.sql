ALTER TABLE `service_accounting_statement`.`transaction_fee`
    ADD COLUMN `fee_name` varchar(32) NULL COMMENT 'Fee name' AFTER `product_code`;