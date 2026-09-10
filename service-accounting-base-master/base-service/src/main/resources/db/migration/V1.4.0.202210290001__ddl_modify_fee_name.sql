ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    MODIFY COLUMN `fee_name` varchar(32) NOT NULL COMMENT 'Fee name' AFTER `calculation_rule`;

ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
    MODIFY COLUMN `fee_name` varchar(32) NOT NULL COMMENT 'Fee name' AFTER `calculation_rule`;