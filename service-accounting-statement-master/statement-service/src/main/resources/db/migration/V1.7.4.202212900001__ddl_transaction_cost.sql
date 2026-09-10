ALTER TABLE `service_accounting_statement`.`transaction_cost`
    ADD COLUMN `country_code`  varchar(3)     NOT NULL COMMENT 'Country code' AFTER `unique_id`,
    ADD COLUMN `merchant_code` varchar(32)    NOT NULL COMMENT 'Merchant code' AFTER `country_code`,
    MODIFY COLUMN `amount_usd` decimal(26, 6) NOT NULL DEFAULT 0 COMMENT 'Amount converted into USD, Unit: cent' AFTER `currency`,
    MODIFY COLUMN `fee_usd` decimal(26, 6) NOT NULL DEFAULT 0 COMMENT 'Transaction fee Amount converted into USD, Unit: cent' AFTER `amount_usd`,
    ADD COLUMN `tax_usd`       decimal(26, 6) NOT NULL DEFAULT 0 COMMENT 'Tax Amount converted into USD, Unit: cent' AFTER `fee_usd`,
    ADD COLUMN `fx_usd`        decimal(26, 6) NOT NULL DEFAULT 0 COMMENT 'Fx Amount converted into USD, Unit: cent' AFTER `tax_usd`,
    ADD COLUMN `fx_rate`       decimal(26, 6) NOT NULL DEFAULT 0 COMMENT 'Exchange to USD rate' AFTER `fx_usd`,
    ADD COLUMN `fx_lose`       decimal(26, 6) NOT NULL DEFAULT 0 COMMENT 'Exchange to USD lose' AFTER `fx_rate`;