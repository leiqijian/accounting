ALTER TABLE `service_accounting_statement`.`account_daily_bill`
    CHANGE COLUMN `transaction_amount` `settlement_amount` decimal(26, 0) NULL DEFAULT 0 COMMENT 'after exchange to account currency amount' AFTER `transaction_count`;

ALTER TABLE `service_accounting_statement`.`account_daily_bill`
    ADD COLUMN `transaction_amount`   decimal(26, 0) NULL DEFAULT 0 COMMENT 'original transaction order amount' AFTER `transaction_count`,
    ADD COLUMN `transaction_currency` varchar(3)     NULL COMMENT 'original transaction order currency' AFTER `transaction_amount`,
    ADD COLUMN `settlement_currency`  varchar(3)     NULL COMMENT 'after exchange to account currency' AFTER `settlement_amount`;

ALTER TABLE `service_accounting_statement`.`account_transfer_config`
    ADD COLUMN `country_code` varchar(3) NOT NULL DEFAULT '' COMMENT 'country code' AFTER `merchant_id`,
    ADD COLUMN `merchant_code` varchar(64) NOT NULL DEFAULT '' COMMENT 'merchant_code' AFTER `country_code`;

UPDATE service_accounting_statement.account_transfer_config atc
    LEFT JOIN service_accounting_base.merchant mer ON (atc.merchant_id = mer.id)
    LEFT JOIN service_accounting_statement.account acc ON (atc.payin_account_id = acc.id)
SET atc.merchant_code = mer.code, atc.country_code = acc.country_code;


ALTER TABLE `service_accounting_statement`.`transaction_fee`
    ADD COLUMN `calculate_amount` decimal(26, 6) NULL DEFAULT 0 COMMENT 'actuality calculate amount, unit: cent(keep 6 decimal places)' AFTER `amount_pon`;

