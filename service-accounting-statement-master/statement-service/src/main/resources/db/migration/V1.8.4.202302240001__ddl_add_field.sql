ALTER TABLE `service_accounting_statement`.`transaction_cost`
    ADD COLUMN `extra_fee_usd`         decimal(26, 6)  NULL DEFAULT 0 COMMENT 'Extra Transaction Fee Amount converted into USD, Unit: cent' AFTER `fee_usd`,
    ADD COLUMN `extra_tax_usd`         decimal(26, 6)  NULL DEFAULT 0 COMMENT 'Extra Transaction Tax Amount converted into USD, Unit: cent' AFTER `tax_usd`,
    ADD COLUMN `extra_fx_usd`          decimal(26, 6)  NULL DEFAULT 0 COMMENT 'Extra Transaction FX Amount converted into USD, Unit: cent' AFTER `fx_usd`,
    ADD COLUMN `transaction_timestamp` bigint UNSIGNED NULL DEFAULT 0 COMMENT 'Transaction timestamp' AFTER `transaction_time`;

ALTER TABLE `service_accounting_statement`.`transaction_cost`
    DROP INDEX `idx_merchant_account`,
    ADD INDEX `idx_merchant_account` (`merchant_id`, `account_id`, `transaction_timestamp`) USING BTREE;