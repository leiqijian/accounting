ALTER TABLE `service_accounting_statement`.`transaction_fee`
    ADD COLUMN `fee_group` varchar(32) NOT NULL DEFAULT 'TRANSACTION_FEE' COMMENT 'FeeGroupEnum: TRANSACTION_FEE, TAX, FX; Since for bill or report classification summary statistics' AFTER `fee_type_code`;

ALTER TABLE `service_accounting_statement`.`transaction_cost`
    ADD COLUMN `operate_source` tinyint(4) UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Operate source 0: OFFLINE, 1:ONLINE' AFTER `vendor`;