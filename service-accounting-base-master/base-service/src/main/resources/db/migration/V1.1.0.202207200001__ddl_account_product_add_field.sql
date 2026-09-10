ALTER TABLE service_accounting_base.account_product
    ADD COLUMN trading_model varchar(6) NULL DEFAULT NULL COMMENT 'just pay-in account trading model: 1, 2, 3 ; eg: D+1, D+2, D+3 ...' AFTER product_code;

UPDATE service_accounting_base.account_product
    SET trading_model='D+1'
WHERE transaction_type_code='PAY_IN'
AND product_code IN ('SPEI','PIX','TED');

UPDATE service_accounting_base.account_product
SET trading_model='D+3'
WHERE transaction_type_code='PAY_IN'
  AND product_code IN ('BOLETO');

UPDATE service_accounting_base.account_product
SET trading_model='D+30'
WHERE transaction_type_code='PAY_IN'
  AND product_code IN ('CREDIT_CARD','ELO_CREDIT_CARD');