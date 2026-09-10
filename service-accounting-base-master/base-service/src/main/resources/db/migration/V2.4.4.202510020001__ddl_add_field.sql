ALTER TABLE service_accounting_base.extra_income_configuration
    ADD COLUMN card_type varchar(16) NULL DEFAULT '' COMMENT 'CardTypeEnum: CREDIT_CARD, DEBIT_CARD' AFTER product_code,
    ADD COLUMN card_group varchar(16)  NULL DEFAULT '' COMMENT 'CardGroupEnum: VISA, MASTERCARD, ELO, AMEX, DEFAULT...' AFTER card_type;


INSERT INTO extra_income_configuration (active_version, account_id, country_code, transaction_type_code, product_code, card_type, card_group, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark) VALUES
( 20250101, 471048525340213378, 'BR', 'PAY_IN', 'CARD', 'CREDIT_CARD', 'VISA', 0, 360, 'FEE', 'TRANSACTION_FEE', 'EXTRA_FEE', 'AMOUNT', 1, 0.0245, 'USD',now(), now(), 0, 0, 1, 0, ''),
( 20250101, 471048525340213378, 'BR', 'PAY_IN', 'CARD', 'CREDIT_CARD', 'VISA', 0, 360, 'PIS/COFINS/ISS ', 'TAX', 'EXTRA_TAX', 'FEE', 1, 0.1268, 'USD', now(), now(), 0, 0, 1, 0, ''),

( 20250101, 471048525340213378, 'BR', 'PAY_IN', 'CARD', 'CREDIT_CARD', 'MASTERCARD', 0, 360, 'FEE', 'TRANSACTION_FEE', 'EXTRA_FEE', 'AMOUNT', 1, 0.0239, 'USD',now(), now(), 0, 0, 1, 0, ''),
( 20250101, 471048525340213378, 'BR', 'PAY_IN', 'CARD', 'CREDIT_CARD', 'MASTERCARD', 0, 360, 'PIS/COFINS/ISS ', 'TAX', 'EXTRA_TAX', 'FEE', 1, 0.1268, 'USD', now(), now(), 0, 0, 1, 0, ''),

( 20250101, 471048525340213378, 'BR', 'PAY_IN', 'CARD', 'CREDIT_CARD', 'DEFAULT', 0, 360, 'FEE', 'TRANSACTION_FEE', 'EXTRA_FEE', 'AMOUNT', 1, 0.0258, 'USD',now(), now(), 0, 0, 1, 0, ''),
( 20250101, 471048525340213378, 'BR', 'PAY_IN', 'CARD', 'CREDIT_CARD', 'DEFAULT', 0, 360, 'PIS/COFINS/ISS ', 'TAX', 'EXTRA_TAX', 'FEE', 1, 0.1268, 'USD', now(), now(), 0, 0, 1, 0, '');
