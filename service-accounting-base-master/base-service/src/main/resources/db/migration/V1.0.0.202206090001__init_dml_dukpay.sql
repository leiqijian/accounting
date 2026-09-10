-- 产品开通(PAY_IN-PIX/PAY_OUT-PIX) 交易额梯度
INSERT INTO `service_accounting_base`.`account_product` (`id`, `account_id`, `transaction_type_code`, `product_code`, `monthly_volume_type`, `timezone`, `open_time`) VALUES
-- dukpay-巴西-PAY_IN-(PIX)
(83869193261547535, 83864700725755919, 'PAY_IN', 'PIX', 0, 'UTC+8', now()),
-- dukpay-巴西-PAY_OUT-(PIX)
(83869193261547536, 83864700725755920, 'PAY_OUT', 'PIX', 0, 'UTC+8', now());


-- 费用配置
-- ==========【 dukpay-巴西-PAY_IN-PIX 】==========
-- ========== TRANSACTION_FEE ==========
-- 0-1000000/1000001-5000000/5000001-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362389190001, 83864700725755919, 83869193261547535, 'PIX', 'TRANSACTION_FEE', 0, 0, 1000001, 0, 50, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362389190002, 83864700725755919, 83869193261547535, 'PIX', 'TRANSACTION_FEE', 0, 1000001, 5000001, 0, 30, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362389190003, 83864700725755919, 83869193261547535, 'PIX', 'TRANSACTION_FEE', 0, 5000001, 99999999999, 0, 20, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND ==========
-- 0-99999999999
(84320362389190004, 83864700725755919, 83869193261547535, 'PIX', 'REFUND_FEE', 0, 0, 99999999999, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 1),
-- ========== CHARGE_BACK ==========
-- 0-99999999999
(84320362389190005, 83864700725755919, 83869193261547535, 'PIX', 'CHARGE_BACK_FEE', 0, 0, 99999999999, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 1);


-- ==========【 dukpay-巴西-PAY_OUT-PIX 】==========
-- ========== TRANSACTION_FEE ==========
-- 0-1000000/1000001-5000000/5000001-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362389200001, 83864700725755920, 83869193261547536, 'PIX', 'TRANSACTION_FEE', 0, 0, 1000001, 0, 50, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362389200002, 83864700725755920, 83869193261547536, 'PIX', 'TRANSACTION_FEE', 0, 1000001, 5000001, 0, 30, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362389200003, 83864700725755920, 83869193261547536, 'PIX', 'TRANSACTION_FEE', 0, 5000001, 99999999999, 0, 20, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND ==========
-- 0-99999999999
(84320362389200004, 83864700725755920, 83869193261547536, 'PIX', 'REFUND_FEE', 0, 0, 99999999999, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 1),
-- ========== CHARGE_BACK ==========
-- 0-99999999999
(84320362389200005, 83864700725755920, 83869193261547536, 'PIX', 'CHARGE_BACK_FEE', 0, 0, 99999999999, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 1);



-- 月度费用配置6月
INSERT INTO `service_accounting_base`.`monthly_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `active_month`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
-- dukpay-巴西-PAY_IN-PIX
-- 6月梯度(0-1000001)
-- ========== TRANSACTION_FEE ==========
(90632134688836029, 83864700725755919, 83869193261547535, 'PIX', 'TRANSACTION_FEE', 202206, 0, 50, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 0),
-- ========== REFUND ==========
(90632134688836030, 83864700725755919, 83869193261547535, 'PIX', 'REFUND_FEE', 202206, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 0),
-- ========== CHARGE_BACK ==========
(90632134688836031, 83864700725755919, 83869193261547535, 'PIX', 'CHARGE_BACK_FEE', 202206, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 0),
-- dukpay-巴西-PAY_OUT-PIX
-- 6月梯度(0-1000001)
-- ========== TRANSACTION_FEE ==========
(90632134688836032, 83864700725755920, 83869193261547536, 'PIX', 'TRANSACTION_FEE', 202206, 0, 50, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 0),
-- ========== REFUND ==========
(90632134688836033, 83864700725755920, 83869193261547536, 'PIX', 'REFUND_FEE', 202206, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 0),
-- ========== CHARGE_BACK ==========
(90632134688836034, 83864700725755920, 83869193261547536, 'PIX', 'CHARGE_BACK_FEE', 202206, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 0);



-- 设置创建时间，修改时间
UPDATE `service_accounting_base`.`account_fee_configuration` SET `created_time` = '2022-01-01 00:00:00', `updated_time` = '2022-01-01 00:00:00' WHERE account_id IN ('83864700725755919','83864700725755920');
UPDATE `service_accounting_base`.`monthly_fee_configuration` SET `created_time` = '2022-06-01 00:00:00', `updated_time` = '2022-06-01 00:00:00' WHERE account_id IN ('83864700725755919','83864700725755920') AND `active_month` = 202206;