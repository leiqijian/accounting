-- BR-KWAI-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BEXS
(83864700725755913, 'BEXS', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.01, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.7, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'TED', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.08, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'TED', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'TED', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.7, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BS2
(83864700725755913, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.07, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2', 'TED', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.07, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2', 'TED', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2', 'TED', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-X6-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BS2
(83864700725755916, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.1, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BS2', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BS2', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- TUNA
(83864700725755916, 'TUNA', 'CREDIT_CARD', 202212, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'TUNA', 'CREDIT_CARD', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-X6-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BEXS
(83864700725755917, 'BEXS', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.01, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BEXS', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BEXS', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.7, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
-- BS2
(83864700725755917, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.07, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BS2', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BS2', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-DUKPAY-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BS2
(83869700725799128, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.1, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799128, 'BS2', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799128, 'BS2', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
-- BANKLY
(83869700725799128, 'BANKLY', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.5, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799128, 'BANKLY', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799128, 'BANKLY', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-DUKPAY-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BEXS
(83869700725799129, 'BEXS', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.01, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799129, 'BEXS', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799129, 'BEXS', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.7, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
-- BS2
(83869700725799129, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.07, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799129, 'BS2', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799129, 'BS2', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-NAVIGATORX-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BEXS
(83864700725755943, 'BEXS', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.01, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755943, 'BEXS', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755943, 'BEXS', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.7, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
-- BS2
(83864700725755943, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.07, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755943, 'BS2', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755943, 'BS2', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-ZUBALE-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BEXS
(83864700725755946, 'BEXS', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.01, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755946, 'BEXS', 'PIX', 202212, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.25, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
-- BS2
(83864700725755946, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.07, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755946, 'BS2', 'PIX', 202212, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.25, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755946, 'BS2', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-WIIO-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BS2
(83864700725755955, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.1, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BS2', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BS2', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
-- BANKLY
(83864700725755955, 'BANKLY', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.5, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BANKLY', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BANKLY', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-GEEWALLET-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BS2
(83864700725755931, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.1, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755931, 'BS2', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755931, 'BS2', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-GEEWALLET-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BEXS
(83864700725755932, 'BEXS', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.01, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755932, 'BEXS', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755932, 'BEXS', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.7, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-TUNA-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BS2
(83864700725799121, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.1, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799121, 'BS2', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

-- BR-PAYERMAX-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BS2
(83864700725755935, 'BS2', 'PIX', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.1, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'BS2', 'PIX', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'BS2', 'PIX', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'BS2', 'BOLETO', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2.47, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'BS2', 'BOLETO', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'BS2', 'BOLETO', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.75, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
-- TUAN
(83864700725755935, 'TUAN', 'CREDIT_CARD', 202212, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.0, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'TUAN', 'CREDIT_CARD', 202212, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.38, 'USD', NOW(), NOW(), 0, 0, 1, 0, '');

/*===============================================================================================================================================*/
-- MX-KWAI-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- UNIPAGOS
(83864700725755911, 'UNIPAGOS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2.8, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755911, 'UNIPAGOS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755911, 'UNIPAGOS', 'SPEI', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.5, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- ARCUS
(83864700725755911, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755911, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755911, 'ARCUS', 'SPEI', 202212, 1, 2, 'FX', 'FX', 'AMOUNT', 0.5, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-Kwai-Giftcard-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- GESTOPAGOS
(83864700725755912, 'GESTOPAGOS', 'SPEI', 202212, 0, 2, 'FX', 'FX', 'AMOUNT', 0.5, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-DUKPAY-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- ARCUS
(83864700725755951, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755951, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-DUKPAY-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- ARCUS
(83864700725755952, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755952, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- UNIPAGOS
(83864700725755952, 'UNIPAGOS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.7, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755952, 'UNIPAGOS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-NANOPAY-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- UNIPAGOS
(83864700725755941, 'UNIPAGOS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.1, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755941, 'UNIPAGOS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-NANOPAY_ARCUS-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- UNIPAGOS
(83864700725755942, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755942, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-NAVIGATORX-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- ARCUS
(83864700725755944, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755944, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-ZUBALE-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- PAYCASH
(83864700725799124, 'PAYCASH', 'PAY_CASH', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 10.5, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799124, 'PAYCASH', 'PAY_CASH', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-ZUBALE-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- ARCUS
(83864700725755947, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755947, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- UNIPAGOS
(83864700725755947, 'UNIPAGOS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.7, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755947, 'UNIPAGOS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-Fintopia-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- UNIPAGOS
(83864700725755948, 'UNIPAGOS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.1, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755948, 'UNIPAGOS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- ARCUS
(83864700725755948, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755948, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- PAYCASH
(83864700725755948, 'PAYCASH', 'PAY_CASH', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 10.5, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755948, 'PAYCASH', 'PAY_CASH', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');


-- MX-Fintopia-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- ARCUS
(83864700725755949, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755949, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- UNIPAGOS
(83864700725755949, 'UNIPAGOS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.7, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755949, 'UNIPAGOS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-Sailone-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- ARCUS
(83864700725755950, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755950, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- PAYCASH
(83864700725755950, 'PAYCASH', 'PAY_CASH', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 10.5, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755950, 'PAYCASH', 'PAY_CASH', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-Sailone-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- ARCUS
(83864700725755945, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755945, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- UNIPAGOS
(83864700725755945, 'UNIPAGOS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.7, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755945, 'UNIPAGOS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-Zwolfre-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- ARCUS
(83864700725755953, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755953, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- PAYCASH
(83864700725755953, 'PAYCASH', 'PAY_CASH', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 10.5, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755953, 'PAYCASH', 'PAY_CASH', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-Zwolfre-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- ARCUS
(83864700725755954, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755954, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- UNIPAGOS
(83864700725755954, 'UNIPAGOS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.7, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755954, 'UNIPAGOS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

-- MX-Payermax-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- ARCUS
(83864700725755937, 'ARCUS', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755937, 'ARCUS', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
-- PAYCASH
(83864700725755937, 'PAYCASH', 'PAY_CASH', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 10.5, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755937, 'PAYCASH', 'PAY_CASH', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 16, 'MXN', NOW(), NOW(), 0, 0, 1, 0, '');

/*===============================================================================================================================================*/
-- CO-ZUBALE-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BANCOLOMBIA
(83864700725799009, 'BANCOLOMBIA', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 500, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799009, 'BANCOLOMBIA', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 19, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799009, 'BANCOLOMBIA', 'SPEI', 202212, 1, 2, 'GMF', 'TAX', 'AMOUNT', 0.4, 'COP', NOW(), NOW(), 0, 0, 1, 0, '');

-- CO-SailoneCo-PAY_IN
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BANCOLOMBIA
(83864700725799010, 'PAYCASH', 'PAY_CASH', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 700, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'PAYCASH', 'PAY_CASH', 202212, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'PAYCASH', 'PAY_CASH', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 19, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'WOMPI', 'PSE', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 850, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'WOMPI', 'PSE', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 19, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'WOMPI', 'NEQUI', 202212, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.5, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'WOMPI', 'NEQUI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 19, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'WOMPI', 'BANCOLOMBIA_BUTTON', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 800, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'WOMPI', 'BANCOLOMBIA_BUTTON', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 19, 'COP', NOW(), NOW(), 0, 0, 1, 0, '');

-- CO-SailoneCo-PAY_OUT
INSERT INTO service_accounting_base.cost_fee_config(account_id, vendor_code, product_code, active_month, cost_model, tax_rule, cost_name, cost_type, cost_on, cost_value, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)VALUES
-- BANCOLOMBIA
(83864700725799011, 'BANCOLOMBIA', 'SPEI', 202212, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 500, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799011, 'BANCOLOMBIA', 'SPEI', 202212, 1, 2, 'IVA', 'TAX', 'FEE', 19, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799011, 'BANCOLOMBIA', 'SPEI', 202212, 0, 2, 'GMF', 'TAX', 'AMOUNT', 0.4, 'COP', NOW(), NOW(), 0, 0, 1, 0, '');

-- convert to cent
UPDATE service_accounting_base.cost_fee_config SET cost_value = cost_value * 100.0 WHERE cost_model=0;
UPDATE service_accounting_base.cost_fee_config SET cost_value = cost_value / 100.0 WHERE cost_model=1;

