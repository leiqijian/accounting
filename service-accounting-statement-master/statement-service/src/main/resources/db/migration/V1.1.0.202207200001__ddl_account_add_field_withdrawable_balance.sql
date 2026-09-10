ALTER TABLE service_accounting_statement.transaction_money
    ADD COLUMN be_credited_date date NULL DEFAULT NULL COMMENT 'to be credited date(settled at date of merchant timezone)' AFTER settle_status,
    ADD COLUMN  be_credited_amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'to be credited amount(deducted fee) unit:cent' AFTER be_credited_date;

ALTER TABLE service_accounting_statement.account
    ADD COLUMN extractable_balance decimal(26, 0) NULL DEFAULT 0 COMMENT 'extractable balance, unit:cent' AFTER sub_total_amount;

ALTER TABLE service_accounting_statement.account_daily_bill
    ADD COLUMN extractable_balance decimal(26, 0) NULL DEFAULT 0 COMMENT 'daily extractable balance, unit:cent' AFTER end_balance;
