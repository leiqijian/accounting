ALTER TABLE `service_accounting_statement`.`transaction_fee`
    MODIFY COLUMN `fee_name` varchar (32) NOT NULL COMMENT 'Fee name' AFTER `product_code`;