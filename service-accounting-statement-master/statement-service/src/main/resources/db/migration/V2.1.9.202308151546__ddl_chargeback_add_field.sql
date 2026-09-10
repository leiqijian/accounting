ALTER TABLE `service_accounting_statement`.`transaction_charge_back_order`
    ADD COLUMN `card_number` varchar(64) NULL AFTER `currency`,
ADD COLUMN `payment_time` datetime(0) NOT NULL COMMENT 'Original order transaction time' AFTER `card_number`;