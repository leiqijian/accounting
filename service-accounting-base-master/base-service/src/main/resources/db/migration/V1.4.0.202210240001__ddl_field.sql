ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    ADD COLUMN `fee_name` varchar(32) NULL COMMENT 'Fee name' AFTER `calculation_rule`;

ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
    ADD COLUMN `fee_name` varchar(32) NULL COMMENT 'Fee name' AFTER `calculation_rule`;