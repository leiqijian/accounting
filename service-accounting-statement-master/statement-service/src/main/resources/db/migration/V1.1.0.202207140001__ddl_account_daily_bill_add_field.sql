ALTER TABLE service_accounting_statement.account_daily_bill
    CHANGE COLUMN settlement_currency transaction_fee_currency varchar (3) DEFAULT NULL COMMENT 'transaction_fee currency' AFTER transaction_tax,
    CHANGE COLUMN unsettlement_fee transaction_fee2 decimal (26, 0) NULL DEFAULT 0 AFTER transaction_fee_currency,
    CHANGE COLUMN unsettlement_tax transaction_tax2 decimal (26, 0) NULL DEFAULT 0 AFTER transaction_fee2,
    CHANGE COLUMN unsettlement_currency transaction_fee2_currency varchar (3) DEFAULT NULL COMMENT 'transaction_fee2 currency' AFTER transaction_tax2;