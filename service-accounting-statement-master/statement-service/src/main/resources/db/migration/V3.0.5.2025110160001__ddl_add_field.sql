/* additional_charge */
ALTER TABLE `service_accounting_statement`.`transaction_money`
    ADD COLUMN `additional_charge` json DEFAULT NULL COMMENT 'additional_charge' AFTER `be_credited_amount`;

ALTER TABLE `service_accounting_statement`.`account_daily_bill`
    ADD COLUMN `additional_charge` decimal(26, 0) NULL DEFAULT 0 COMMENT 'additional_charge' AFTER `settlement_amount`;
