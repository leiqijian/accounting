ALTER TABLE `service_accounting_statement`.`transaction_charge_back_order`
    CHANGE COLUMN `chargeback_time` `dispute_time` datetime(0) NOT NULL COMMENT 'UTC+0' AFTER `payment_time`;