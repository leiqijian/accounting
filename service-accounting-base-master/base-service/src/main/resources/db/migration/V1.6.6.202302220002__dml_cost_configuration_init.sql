DELETE FROM service_accounting_base.cost_configuration;

INSERT INTO service_accounting_base.cost_configuration(account_id, vendor_code, product_code, active_month, apply_on, fee_model, tax_rule,
                                                       fee_name, fee_type, fee_on, volume, currency,created_time, updated_time, created_by,
                                                       updated_by, version, del_flag, remark) VALUES
-- BR-KWAI-PAY_OUT
(83864700725755913, 'BEXS', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.010000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.700000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755913, 'BEXS', 'TED', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.080000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'TED', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'TED', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.700000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755913, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.070000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755913, 'BS2', 'TED', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.070000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2', 'TED', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2', 'TED', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-KWAI-PAY_OUT 收入后付费补入
(83864700725755913, 'BEXS', 'PIX', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'TED', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2',  'PIX', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2',  'TED', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'PIX', 202302, 1, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.005000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BEXS', 'TED', 202302, 1, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.005000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2',  'PIX', 202302, 1, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.005000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755913, 'BS2',  'TED', 202302, 1, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.005000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),


-- BR-X6-PAY_IN
(83864700725755916, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.100000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BS2', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BS2', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755916, 'TUNA', 'CREDIT_CARD', 202302, 0, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.000000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'TUNA', 'CREDIT_CARD', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755916, 'BEXS', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.010000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BEXS', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BEXS', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755916, 'BEXS', 'CREDIT_CARD', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.010000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BEXS', 'CREDIT_CARD', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BEXS', 'CREDIT_CARD', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-X6-PAY_IN 收入补入
(83864700725755916, 'BEXS', 'PIX', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BEXS', 'TED', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BS2',  'PIX', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BS2',  'TED', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'TUNA', 'CREDIT_CARD', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BEXS', 'CREDIT_CARD', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755916, 'BEXS', 'PIX', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 1.800000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BEXS', 'TED', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 1.800000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BS2',  'PIX', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 1.800000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BS2',  'TED', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 1.800000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'TUNA', 'CREDIT_CARD', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 1.800000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755916, 'BEXS', 'CREDIT_CARD', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 1.800000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-X6-PAY_OUT
(83864700725755917, 'BEXS', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.010000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BEXS', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BEXS', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.700000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755917, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.070000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BS2', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BS2', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-X6-PAY_OUT 收入补入
(83864700725755917, 'BEXS', 'PIX', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BEXS', 'TED', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BS2',  'PIX', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BS2',  'TED', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755917, 'BEXS', 'PIX', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 1.800000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BEXS', 'TED', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 1.800000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BS2',  'PIX', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 1.800000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755917, 'BS2',  'TED', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 1.800000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-DUKPAY-PAY_IN
(83869700725799128, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.100000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799128, 'BS2', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799128, 'BS2', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83869700725799128, 'BANKLY', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.500000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799128, 'BANKLY', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799128, 'BANKLY', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-DUKPAY-PAY_OUT
(83869700725799129, 'BEXS', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.010000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799129, 'BEXS', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799129, 'BEXS', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.700000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83869700725799129, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.070000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799129, 'BS2', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725799129, 'BS2', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-NAVIGATORX-PAY_OUT
(83864700725755943, 'BEXS', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.010000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755943, 'BEXS', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755943, 'BEXS', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.700000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755943, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.070000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755943, 'BS2', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755943, 'BS2', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-ZUBALE-PAY_OUT
(83864700725755946, 'BEXS', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.010000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755946, 'BEXS', 'PIX', 202302, 0, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.250000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755946, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.070000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755946, 'BS2', 'PIX', 202302, 0, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.250000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-WIIO-PAY_IN
(83864700725755955, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.100000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BS2', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BS2', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755955, 'BANKLY', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.500000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BANKLY', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BANKLY', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-WIIO-PAY_IN 收入补入
(83864700725755955, 'BS2',  'PIX', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BANKLY', 'PIX', 202302, 1, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BS2', 'PIX', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 2.000000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755955, 'BANKLY', 'PIX', 202302, 1, 0, 2, 'FX_LOSE', 'FX_LOSE', 'AMOUNT', 2.000000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-GEEWALLET-PAY_IN
(83864700725755931, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.100000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755931, 'BS2', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755931, 'BS2', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-GEEWALLET-PAY_OUT
(83864700725755932, 'BEXS', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.010000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755932, 'BEXS', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755932, 'BEXS', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.700000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-TUNA-PAY_IN
(83864700725799121, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.100000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799121, 'BS2', 'PIX', 202302, 0, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.250000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-PAYERMAX-PAY_IN
(83864700725755935, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.100000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'BS2', 'PIX', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'BS2', 'PIX', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755935, 'BS2', 'BOLETO', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2.470000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'BS2', 'BOLETO', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'BS2', 'BOLETO', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.750000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755935, 'TUAN', 'CREDIT_CARD', 202302, 0, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.000000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755935, 'TUAN', 'CREDIT_CARD', 202302, 0, 1, 2, 'IOF', 'TAX', 'AMOUNT', 0.380000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-GlobalPix-PAY_IN
(93869700725939148, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.100000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(93869700725939148, 'BS2', 'PIX', 202302, 0, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.250000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(93869700725939148, 'BS2', 'BOLETO', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2.470000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(93869700725939148, 'BS2', 'BOLETO', 202302, 0, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.250000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(93869700725939148, 'BANKLY', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.500000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(93869700725939148, 'BANKLY', 'PIX', 202302, 0, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.250000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(93869700725939148, 'BANKLY', 'BOLETO', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2.700000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(93869700725939148, 'BANKLY', 'BOLETO', 202302, 0, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.250000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

-- BR-GlobalPix-PAY_OUT
(93869700725939149, 'BS2', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.070000, 'BRL', NOW(), NOW(), 0, 0, 1, 0, ''),
(93869700725939149, 'BS2', 'PIX', 202302, 0, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.250000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

(93869700725939149, 'BEXS', 'PIX', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 0.010000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(93869700725939149, 'BEXS', 'PIX', 202302, 0, 1, 1, 'PIS/CONFINS/ISS', 'TAX', 'AMOUNT', 11.250000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

/*===============================================================================================================================================*/
-- MX-KWAI-PAY_OUT
(83864700725755911, 'UNIPAGOS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2.800000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755911, 'UNIPAGOS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755911, 'UNIPAGOS', 'SPEI', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.500000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755911, 'ARCUS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755911, 'ARCUS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755911, 'ARCUS', 'SPEI', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.500000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-Kwai-Giftcard-PAY_OUT
(83864700725755912, 'GESTOPAGOS', 'SPEI', 202302, 0, 1, 2, 'FX', 'FX', 'AMOUNT', 0.500000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-DUKPAY-PAY_IN
(83864700725755951, 'ARCUS', 'SPEI_VA', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755951, 'ARCUS', 'SPEI_VA', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-DUKPAY-PAY_OUT
(83864700725755952, 'ARCUS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755952, 'ARCUS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755952, 'UNIPAGOS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.700000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755952, 'UNIPAGOS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-NANOPAY-PAY_IN
(83864700725755941, 'UNIPAGOS', 'SPEI_VA', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.100000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755941, 'UNIPAGOS', 'SPEI_VA', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-NANOPAY_ARCUS-PAY_IN
(83864700725755942, 'ARCUS', 'SPEI_VA', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755942, 'ARCUS', 'SPEI_VA', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-NAVIGATORX-PAY_OUT
(83864700725755944, 'ARCUS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755944, 'ARCUS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-ZUBALE-PAY_IN
(83864700725799124, 'PAYCASH', 'PAY_CASH', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 10.500000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799124, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-Sailone-PAY_OUT
(83864700725755945, 'ARCUS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755945, 'ARCUS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755945, 'UNIPAGOS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.700000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755945, 'UNIPAGOS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-ZUBALE-PAY_OUT
(83864700725755947, 'ARCUS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755947, 'ARCUS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755947, 'UNIPAGOS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.700000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755947, 'UNIPAGOS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-Fintopia-PAY_IN
(83864700725755948, 'UNIPAGOS', 'SPEI_VA', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.100000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755948, 'UNIPAGOS', 'SPEI_VA', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755948, 'ARCUS', 'SPEI_VA', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755948, 'ARCUS', 'SPEI_VA', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755948, 'PAYCASH', 'PAY_CASH', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 10.500000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755948, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-Fintopia-PAY_OUT
(83864700725755949, 'ARCUS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755949, 'ARCUS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755949, 'UNIPAGOS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.700000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755949, 'UNIPAGOS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-Sailone-PAY_IN
(83864700725755950, 'ARCUS', 'SPEI_VA', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755950, 'ARCUS', 'SPEI_VA', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755950, 'PAYCASH', 'PAY_CASH', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 10.500000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755950, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-Zwolfre-PAY_IN
(83864700725755953, 'ARCUS', 'SPEI_VA', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755953, 'ARCUS', 'SPEI_VA', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755953, 'PAYCASH', 'PAY_CASH', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 10.500000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755953, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-Zwolfre-PAY_OUT
(83864700725755954, 'ARCUS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755954, 'ARCUS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755954, 'UNIPAGOS', 'SPEI', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 3.700000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755954, 'UNIPAGOS', 'SPEI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

-- MX-Payermax-PAY_IN
(83864700725755937, 'ARCUS', 'SPEI_VA', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755937, 'ARCUS', 'SPEI_VA', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725755937, 'PAYCASH', 'PAY_CASH', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 10.500000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725755937, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 16.000000, 'MXN', NOW(), NOW(), 0, 0, 1, 0, ''),

/*===============================================================================================================================================*/
-- CO-ZUBALE-PAY_OUT
(83864700725799009, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 500.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799009, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799009, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 1, 2, 'GMF', 'TAX', 'AMOUNT', 0.400000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

-- CO-SailoneCo-PAY_IN
(83864700725799010, 'PAYCASH', 'PAY_CASH', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 700.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725799010, 'WOMPI', 'PSE', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 850.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'WOMPI', 'PSE', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725799010, 'WOMPI', 'NEQUI', 202302, 0, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.500000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'WOMPI', 'NEQUI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

(83864700725799010, 'WOMPI', 'BANCOLOMBIA_BUTTON', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 800.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799010, 'WOMPI', 'BANCOLOMBIA_BUTTON', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

-- CO-SailoneCo-PAY_OUT
(83864700725799011, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 500.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799011, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83864700725799011, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 1, 2, 'GMF', 'TAX', 'AMOUNT', 0.400000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),


-- CO-Sailone_rapicredi-PAY_IN
(83869700725839144, 'PAYCASH', 'PAY_CASH', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 700.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839144, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839144, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

(83869700725839144, 'WOMPI', 'PSE', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 850.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839144, 'WOMPI', 'PSE', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

(83869700725839144, 'WOMPI', 'NEQUI', 202302, 0, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.500000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839144, 'WOMPI', 'NEQUI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

(83869700725839144, 'WOMPI', 'BANCOLOMBIA_BUTTON', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 800.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839144, 'WOMPI', 'BANCOLOMBIA_BUTTON', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

-- CO-Sailone_rapicredi-PAY_OUT
(83869700725839145, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 500.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839145, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839145, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 1, 2, 'GMF', 'TAX', 'AMOUNT', 0.400000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

-- CO-Sailone_Suplerplata-PAY_IN
(83869700725839142, 'PAYCASH', 'PAY_CASH', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 700.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839142, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 2.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839142, 'PAYCASH', 'PAY_CASH', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

(83869700725839142, 'WOMPI', 'PSE', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 850.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839142, 'WOMPI', 'PSE', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

(83869700725839142, 'WOMPI', 'NEQUI', 202302, 0, 1, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 1.500000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839142, 'WOMPI', 'NEQUI', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

(83869700725839142, 'WOMPI', 'BANCOLOMBIA_BUTTON', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 800.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839142, 'WOMPI', 'BANCOLOMBIA_BUTTON', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),

-- CO-Sailone_Suplerplata-PAY_OUT
(83869700725839143, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 0, 2, 'FEE', 'TRANSACTION_FEE', 'AMOUNT', 500.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839143, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 1, 2, 'IVA', 'TAX', 'FEE', 19.000000, 'COP', NOW(), NOW(), 0, 0, 1, 0, ''),
(83869700725839143, 'BANCOLOMBIA', 'CO_BANK_TRANSFER', 202302, 0, 1, 2, 'GMF', 'TAX', 'AMOUNT', 0.400000, 'COP', NOW(), NOW(), 0, 0, 1, 0, '');

-- update volume on cost
UPDATE service_accounting_base.cost_configuration SET volume = volume * 100.0 WHERE apply_on=0 AND fee_model=0;
UPDATE service_accounting_base.cost_configuration SET volume = volume / 100.0 WHERE apply_on=0 AND fee_model=1;

-- update volume on revenue
UPDATE service_accounting_base.cost_configuration SET volume = volume / 100.0 WHERE apply_on=1 AND fee_model=0 AND fee_type='FX_LOSE';

-- copy same config form active_month=202302 to 202303
INSERT INTO service_accounting_base.cost_configuration(account_id, vendor_code, product_code, active_month, apply_on, fee_model, tax_rule,
                                                       fee_name, fee_type, fee_on, volume, currency,created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT account_id, vendor_code, product_code, 202303 AS active_month, apply_on, fee_model, tax_rule,
       fee_name, fee_type, fee_on, volume, currency,created_time, updated_time, created_by, updated_by, version, del_flag, remark
FROM service_accounting_base.cost_configuration WHERE active_month=202302;

-- copy same config form active_month=202302 to 202304
INSERT INTO service_accounting_base.cost_configuration(account_id, vendor_code, product_code, active_month, apply_on, fee_model, tax_rule,
                                                       fee_name, fee_type, fee_on, volume, currency,created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT account_id, vendor_code, product_code, 202304 AS active_month, apply_on, fee_model, tax_rule,
       fee_name, fee_type, fee_on, volume, currency,created_time, updated_time, created_by, updated_by, version, del_flag, remark
FROM service_accounting_base.cost_configuration WHERE active_month=202302;

-- copy same config form active_month=202302 to 202305
INSERT INTO service_accounting_base.cost_configuration(account_id, vendor_code, product_code, active_month, apply_on, fee_model, tax_rule,
                                                       fee_name, fee_type, fee_on, volume, currency,created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT account_id, vendor_code, product_code, 202305 AS active_month, apply_on, fee_model, tax_rule,
       fee_name, fee_type, fee_on, volume, currency,created_time, updated_time, created_by, updated_by, version, del_flag, remark
FROM service_accounting_base.cost_configuration WHERE active_month=202302;

-- copy same config form active_month=202302 to 202306
INSERT INTO service_accounting_base.cost_configuration(account_id, vendor_code, product_code, active_month, apply_on, fee_model, tax_rule,
                                                       fee_name, fee_type, fee_on, volume, currency,created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT account_id, vendor_code, product_code, 202306 AS active_month, apply_on, fee_model, tax_rule,
       fee_name, fee_type, fee_on, volume, currency,created_time, updated_time, created_by, updated_by, version, del_flag, remark
FROM service_accounting_base.cost_configuration WHERE active_month=202302;