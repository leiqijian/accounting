ALTER TABLE `service_accounting_statement`.`transaction_summary`
    CHANGE COLUMN `transaction_money` `transaction_amount` decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'daily total transaction amount' AFTER transaction_date,
    CHANGE COLUMN `transaction_amount` `settlement_amount` decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'daily total settlement amount(excluded fee), formula: transaction_amount * exchange_rate' AFTER exchange_rate,
    CHANGE COLUMN `transaction_amount_usd` `settlement_amount_usd` decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'daily total settlement amount converted into USD, unit: cent' AFTER settlement_amount,
    ADD COLUMN `transaction_volume_amount`  decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'sum((abs)settled transaction amount) - sum((abs)other status transaction amount)' AFTER `transaction_amount`,
    ADD COLUMN `settlement_volume_amount`  decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'sum((abs)settled settlement amount) - sum((abs)other status settlement amount)' AFTER `settlement_amount`,
    ADD COLUMN `settlement_volume_amount_usd`  decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'settlement volume amount converted into USD, unit: cent' AFTER `settlement_amount_usd`;