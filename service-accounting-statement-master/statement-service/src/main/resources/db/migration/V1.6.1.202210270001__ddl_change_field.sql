ALTER TABLE service_accounting_statement.biz_transfer_out
    CHANGE COLUMN product_code payment_channel varchar(32) NULL DEFAULT NULL COMMENT 'payment channel PIX, SPEI...' AFTER amount;