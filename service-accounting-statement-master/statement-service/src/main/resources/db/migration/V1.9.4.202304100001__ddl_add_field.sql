ALTER TABLE `service_accounting_statement`.`transaction_biz`
    MODIFY COLUMN `amount_pon` decimal(2, 0) NOT NULL COMMENT 'positive or negative; positive :+1, negative:-1' AFTER `transaction_time`,
    ADD COLUMN `transaction_amount_usd` decimal(26, 6) NULL DEFAULT 0 COMMENT 'transaction amount convert to usd, unit:cent' AFTER `transaction_currency`,
    ADD COLUMN `exchange_lose`          decimal(26, 6) NULL DEFAULT 0 COMMENT 'exchange lose' AFTER `exchange_rate`,
    ADD COLUMN `exchange_rate_usd`      decimal(26, 6) NULL DEFAULT 1 COMMENT 'exchange rate usd' AFTER `exchange_lose`,
    ADD COLUMN `settlement_amount_usd`  decimal(26, 6) NULL DEFAULT 0 COMMENT 'settlement amount convert to usd, unit:cent' AFTER `settlement_currency`,
    ADD COLUMN `fee_amount_usd`         decimal(26, 6) NULL DEFAULT 0 COMMENT 'fee amount convert to usd, unit:cent' AFTER `fee_amount`,
    ADD COLUMN `tax_amount_usd`         decimal(26, 6) NULL DEFAULT 0 COMMENT 'tax amount convert to usd, unit:cent' AFTER `tax_amount`;