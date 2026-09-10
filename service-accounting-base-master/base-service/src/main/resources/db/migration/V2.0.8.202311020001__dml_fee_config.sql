-- 费用配置：设置account_currency 和 source_currency
ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    CHANGE COLUMN `currency` `account_currency` varchar(3) NOT NULL COMMENT 'account currency' AFTER `fee_value`,
    ADD COLUMN `source_currency` varchar(255) NOT NULL COMMENT 'order source currency' AFTER `account_currency`;


-- 月度费用配置：设置account_currency 和 source_currency
ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
    CHANGE COLUMN `currency` `account_currency` varchar(3) NOT NULL COMMENT 'account currency' AFTER `fee_value`,
    ADD COLUMN `source_currency` varchar(255) NOT NULL COMMENT 'order source currency' AFTER `account_currency`;