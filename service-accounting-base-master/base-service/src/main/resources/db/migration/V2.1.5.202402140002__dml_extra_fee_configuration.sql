INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark) VALUES

/* KWAI PAYIN */
(202401, 83864700725755925, 'BR', 'PAY_IN', 	'PIX', 0, 360, 'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 0, 0.500000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 83864700725755925, 'BR', 'PAY_IN', 	'PIX', 0, 360, 'IOF', 	  'TAX', 		     'EXTRA_TAX',  'AMOUNT', 1, 0.003800, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 83864700725755925, 'BR', 'PAY_IN', 	'TED', 0, 360, 'IOF', 	  'TAX', 		     'EXTRA_TAX',  'AMOUNT', 1, 0.003800, 'USD', now(), now(), 0, 0, 1, 0, ''),

/* KWAI PAYOUT */
(202401, 83864700725755913, 'BR', 'PAY_OUT', 	'PIX', 0, 360, 'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 0, 0.500000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 83864700725755913, 'BR', 'PAY_OUT', 	'PIX', 0, 360, 'IOF', 	  'TAX', 		     'EXTRA_TAX',  'AMOUNT', 1, 0.003800, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 83864700725755913, 'BR', 'PAY_OUT', 	'TED', 0, 360, 'IOF', 	  'TAX', 		     'EXTRA_TAX',  'AMOUNT', 1, 0.003800, 'USD', now(), now(), 0, 0, 1, 0, ''),

/* KWAI_LARGETICKET PAY_OUT */
(202401, 93869700725939335, 'BR', 'PAY_OUT', 	'PIX', 0, 360, 'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 0, 0.500000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939335, 'BR', 'PAY_OUT', 	'PIX', 0, 360, 'FX', 	  'FX', 		     'FX',         'AMOUNT', 1, 0.011300, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939335, 'BR', 'PAY_OUT', 	'TED', 0, 360, 'FX_LOSE', 'FX_LOSE', 		 'FX_LOSE',    'AMOUNT', 0, 0.011300, 'USD', now(), now(), 0, 0, 1, 0, ''),

/* KWAI_SPORTS PAY_OUT */
(202401, 471048525340213284, 'BR', 'PAY_OUT', 	'PIX', 0, 360, 'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 0, 0.500000, 'USD', now(), now(), 0, 0, 1, 0, ''),

/* WIIO PAYIN */
(202401, 83864700725755955, 'BR', 'PAY_IN', 	'',  0, 360, 'IOF', 	  'TAX', 		     'EXTRA_TAX',  'AMOUNT', 1, 0.003800, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 83864700725755955, 'BR', 'PAY_IN', 	'',  0, 360, 'FX', 	      'FX', 		     'FX', 		   'AMOUNT', 1, 0.020000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 83864700725755955, 'BR', 'PAY_IN', 	'',  0, 360, 'FX_LOSE',   'FX_LOSE', 	     'FX_LOSE',    'AMOUNT', 0, 0.020000, 'USD', now(), now(), 0, 0, 1, 0, ''),

/* Durablewig PAYIN */
(202401, 93869700725939302, 'BR', 'PAY_IN',     'CARD', 1, 1, 'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.025500, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939302, 'BR', 'PAY_IN',     'CARD', 2, 2, 'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.027000, 'USD', now(), now(), 0, 0, 1, 0, ''),

/* Ats PAYIN */
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 2, 2,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.029500, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 3, 3,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.038600, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 4, 4,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.047400, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 5, 5,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.056000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 6, 6,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.065500, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 7, 7,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.077500, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 8, 8,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.087000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 9, 9,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.096000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 10, 10, 'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.105400, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 11, 11, 'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.111000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939363, 'BR', 'PAY_IN',     'CARD', 12, 12, 'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.124000, 'USD', now(), now(), 0, 0, 1, 0, ''),

(202401, 93869700725939365, 'MX', 'PAY_IN',     'CARD', 3, 3,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.055000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939365, 'MX', 'PAY_IN',     'CARD', 6, 6,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.085000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939365, 'MX', 'PAY_IN',     'CARD', 9, 9,   'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.115000, 'USD', now(), now(), 0, 0, 1, 0, ''),
(202401, 93869700725939365, 'MX', 'PAY_IN',     'CARD', 12, 12, 'FEE', 	  'TRANSACTION_FEE', 'EXTRA_FEE',  'AMOUNT', 1, 0.150000, 'USD', now(), now(), 0, 0, 1, 0, '');


INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202402, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;

INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202403, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;

INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202404, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;

INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202405, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;

INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202406, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;

INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202407, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;

INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202408, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;

INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202409, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;

INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202410, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;

INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202411, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;

INSERT INTO service_accounting_base.extra_income_configuration (active_month, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 202412, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark FROM extra_income_configuration WHERE active_month=202401;








