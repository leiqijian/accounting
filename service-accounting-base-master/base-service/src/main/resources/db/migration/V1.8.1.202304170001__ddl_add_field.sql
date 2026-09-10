ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    ADD COLUMN `fee_group` varchar(32) NOT NULL DEFAULT 'TRANSACTION_FEE' COMMENT 'FeeGroupEnum: TRANSACTION_FEE, TAX, FX; Since for bill or report classification summary statistics' AFTER `fee_type_code`;

ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
    ADD COLUMN `fee_group` varchar(32) NOT NULL DEFAULT 'TRANSACTION_FEE' COMMENT 'FeeGroupEnum: TRANSACTION_FEE, TAX, FX; Since for bill or report classification summary statistics' AFTER `fee_type_code`;

