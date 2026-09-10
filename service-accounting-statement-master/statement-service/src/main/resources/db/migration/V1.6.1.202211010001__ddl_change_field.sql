ALTER TABLE service_accounting_statement.transaction_payout
    CHANGE COLUMN product_code payment_channel varchar(32) NULL DEFAULT NULL COMMENT 'payment channel PIX, SPEI...' AFTER country_code;