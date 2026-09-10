ALTER TABLE service_accounting_statement.account_daily_bill
    ADD COLUMN settlement_currency varchar(3) NULL COMMENT 'instant time settlemnt currrency' AFTER refund_tax,
    ADD COLUMN  unsettlement_currency varchar(3) NULL COMMENT 'non-instant time settlemnt currrency' AFTER settlement_currency,
    ADD COLUMN  unsettlement_fee decimal(26, 0) NULL DEFAULT 0 AFTER unsettlement_currency,
    ADD COLUMN  unsettlement_tax decimal(26, 0) NULL DEFAULT 0 AFTER unsettlement_fee;

-- setting data
UPDATE service_accounting_statement.account_daily_bill AS bill
    JOIN service_accounting_statement.account AS act ON (bill.account_id = act.id)
SET bill.settlement_currency = act.currency;
