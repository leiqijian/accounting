ALTER TABLE service_accounting_statement.transaction_money
    MODIFY COLUMN amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'transaction amount, unit: cent' AFTER amount_pon,
    MODIFY COLUMN currency varchar(3) NULL DEFAULT NULL COMMENT 'transaction currency' AFTER amount,
    MODIFY COLUMN fx_rate decimal(26, 6) NULL DEFAULT 0 COMMENT 'exchange rate' AFTER currency,
    MODIFY COLUMN settlement_amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'Amount converted into account currency, unit: cent; formula=transactionAmount*fxRate' AFTER fx_rate,
    ADD COLUMN settlement_amount_usd decimal(26, 0) NULL DEFAULT 0 COMMENT 'Amount converted into USD, unit: cent' AFTER settlement_amount,
    MODIFY COLUMN settlement_currency varchar(3) NULL DEFAULT NULL COMMENT 'settlement currency' AFTER settlement_amount;


ALTER TABLE service_accounting_statement.transaction_summary
    CHANGE COLUMN transaction_amount transaction_money decimal(26, 0) NULL DEFAULT 0 COMMENT 'daily total  transaction money' AFTER transaction_date,
    CHANGE COLUMN settlement_amount transaction_amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'daily total transaction amount(excluded fee), formula: transaction_money * exchange_rate' AFTER exchange_rate,
    ADD COLUMN transaction_amount_usd decimal(26, 0) NULL DEFAULT 0 COMMENT 'daily amount converted into USD, unit: cent' AFTER transaction_amount;