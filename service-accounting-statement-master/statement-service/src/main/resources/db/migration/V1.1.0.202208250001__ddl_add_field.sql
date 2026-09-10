ALTER TABLE service_accounting_statement.account_daily_bill
    MODIFY COLUMN recorded_amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'The amount that can be extractable in the account today(bill_date add 1 day)' AFTER extractable_balance;

ALTER TABLE service_accounting_statement.transaction_money
    ADD COLUMN document_id varchar(40)      NULL     DEFAULT '' COMMENT 'document id certificate No, e.g: CPF, CNPJ' AFTER account_id,
    ADD COLUMN hold_status tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT 'hold status, 0:OFF, 1:ON, default:OFF' AFTER be_credited_amount;

ALTER TABLE service_accounting_statement.account
    ADD COLUMN holding_limit decimal(26, 0) UNSIGNED NULL DEFAULT 0 COMMENT 'Monthly limit threshold, unit:cent; Amount to holding when the transaction triggers the threshold' AFTER extractable_balance;