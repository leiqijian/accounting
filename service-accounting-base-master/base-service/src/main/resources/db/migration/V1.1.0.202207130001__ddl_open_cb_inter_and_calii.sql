-- 清除数据
DELETE FROM `service_accounting_base`.`account_product` WHERE `account_id`
IN (83864700725799007, 83864700725799008, 83864700725755923, 83864700725755924);
DELETE FROM `service_accounting_base`.`account_fee_configuration` WHERE `account_id`
IN (83864700725799007, 83864700725799008, 83864700725755923, 83864700725755924);

-- 产品开通 交易量梯度-1
-- cb_inter-巴西-PAY_IN-(PIX/TED/CREDE_CARD/ELO-CREDE_CARD/BOLETO)
INSERT INTO `service_accounting_base`.`account_product` (`id`, `account_id`, `transaction_type_code`, `product_code`, `monthly_volume_type`, `timezone`, `open_time`) VALUES
(83869193261548000, 83864700725799007, 'PAY_IN', 'PIX', 1, 'UTC-3', now()),
(83869193261548001, 83864700725799007, 'PAY_IN', 'TED', 1, 'UTC-3', now()),
(83869193261548002, 83864700725799007, 'PAY_IN', 'CREDIT_CARD', 1, 'UTC-3', now()),
(83869193261548003, 83864700725799007, 'PAY_IN', 'ELO_CREDIT_CARD', 1, 'UTC-3', now()),
(83869193261548004, 83864700725799007, 'PAY_IN', 'BOLETO', 1, 'UTC-3', now()),
-- cb_inter-巴西-PAY_OUT-(PIX/TED)
(83869193261548005, 83864700725799008, 'PAY_OUT', 'PIX', 1, 'UTC-3', now()),
(83869193261548006, 83864700725799008, 'PAY_OUT', 'TED', 1, 'UTC-3', now());

-- 费用配置
-- ==========【 cb_inter-巴西-PAY_IN-PIX 】==========
-- ========== TRANSACTION_FEE ==========
-- 0-1000000/1000001-5000000/5000001-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362390002001, 83864700725799007, 83869193261548000, 'PIX', 'TRANSACTION_FEE', 1, 0, 1000001, 0, 50, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390002002, 83864700725799007, 83869193261548000, 'PIX', 'TRANSACTION_FEE', 1, 1000001, 5000001, 0, 30, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390002003, 83864700725799007, 83869193261548000, 'PIX', 'TRANSACTION_FEE', 1, 5000001, 99999999999, 0, 20, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND ==========
-- 0-99999999999
(84320362390002004, 83864700725799007, 83869193261548000, 'PIX', 'REFUND_FEE', 1, 0, 99999999999, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 1),
-- ========== CHARGE_BACK ==========
-- 0-99999999999
(84320362390002005, 83864700725799007, 83869193261548000, 'PIX', 'CHARGE_BACK_FEE', 1, 0, 99999999999, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 1);


-- ==========【 cb_inter-巴西-PAY_IN-TED 】==========
-- 0-1000000/1000001-5000000/5000001-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362390003001, 83864700725799007, 83869193261548001, 'TED', 'TRANSACTION_FEE', 1, 0, 1000001, 0, 130, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390003002, 83864700725799007, 83869193261548001, 'TED', 'TRANSACTION_FEE', 1, 1000001, 5000001, 0, 110, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390003003, 83864700725799007, 83869193261548001, 'TED', 'TRANSACTION_FEE', 1, 5000001, 99999999999, 0, 80, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND ==========
-- 0-99999999999
(84320362390003004, 83864700725799007, 83869193261548001, 'TED', 'REFUND_FEE', 1, 0, 99999999999, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 1),
-- ========== CHARGE_BACK ==========
-- 0-99999999999
(84320362390003005, 83864700725799007, 83869193261548001, 'TED', 'CHARGE_BACK_FEE', 1, 0, 99999999999, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 1);


-- ==========【 cb_inter-巴西-PAY_IN-CREDIT_CARD 】==========
-- ========== TRANSACTION_FEE ==========
-- 0-1000000/1000001-5000000/5000001-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362390004001, 83864700725799007, 83869193261548002, 'CREDIT_CARD', 'TRANSACTION_FEE', 1, 0, 1000001, 1, 0.028, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390004002, 83864700725799007, 83869193261548002, 'CREDIT_CARD', 'TRANSACTION_FEE', 1, 1000001, 5000001, 1, 0.026, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390004003, 83864700725799007, 83869193261548002, 'CREDIT_CARD', 'TRANSACTION_FEE', 1, 5000001, 99999999999, 1, 0.024, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND ==========
-- 0-99999999999
(84320362390004004, 83864700725799007, 83869193261548002, 'CREDIT_CARD', 'REFUND_FEE', 1, 0, 99999999999, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 1),
-- ========== CHARGE_BACK ==========
-- 0-99999999999
(84320362390004005, 83864700725799007, 83869193261548002, 'CREDIT_CARD', 'CHARGE_BACK_FEE', 1, 0, 99999999999, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 1);



-- ==========【 cb_inter-巴西-PAY_IN-ELO_CREDIT_CARD 】==========
-- ========== TRANSACTION_FEE ==========
-- 0-1000000/1000001-5000000/5000001-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362390005001, 83864700725799007, 83869193261548003, 'ELO_CREDIT_CARD', 'TRANSACTION_FEE', 1, 0, 1000001, 1, 0.032, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390005002, 83864700725799007, 83869193261548003, 'ELO_CREDIT_CARD', 'TRANSACTION_FEE', 1, 1000001, 5000001, 1, 0.03, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390005003, 83864700725799007, 83869193261548003, 'ELO_CREDIT_CARD', 'TRANSACTION_FEE', 1, 5000001, 99999999999, 1, 0.028, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND ==========
-- 0-99999999999
(84320362390005004, 83864700725799007, 83869193261548003, 'ELO_CREDIT_CARD', 'REFUND_FEE', 1, 0, 99999999999, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 1),
-- ========== CHARGE_BACK ==========
-- 0-99999999999
(84320362390005005, 83864700725799007, 83869193261548003, 'ELO_CREDIT_CARD', 'CHARGE_BACK_FEE', 1, 0, 99999999999, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 1);


-- ==========【 cb_inter-巴西-PAY_IN-BOLETO 】==========
-- ========== TRANSACTION_FEE ==========
-- 0-1000000/1000001-5000000/5000001-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362390006001, 83864700725799007, 83869193261548004, 'BOLETO', 'TRANSACTION_FEE', 1, 0, 1000001, 0, 80, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390006002, 83864700725799007, 83869193261548004, 'BOLETO', 'TRANSACTION_FEE', 1, 0, 1000001, 1, 0.02, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390006003, 83864700725799007, 83869193261548004, 'BOLETO', 'TRANSACTION_FEE', 1, 1000001, 5000001, 0, 80, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390006004, 83864700725799007, 83869193261548004, 'BOLETO', 'TRANSACTION_FEE', 1, 1000001, 5000001, 1, 0.018, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390006005, 83864700725799007, 83869193261548004, 'BOLETO', 'TRANSACTION_FEE', 1, 5000001, 99999999999, 0, 80, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390006006, 83864700725799007, 83869193261548004, 'BOLETO', 'TRANSACTION_FEE', 1, 5000001, 99999999999, 1, 0.016, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND ==========
-- 0-99999999999
(84320362390006007, 83864700725799007, 83869193261548004, 'BOLETO', 'REFUND_FEE', 1, 0, 99999999999, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 1),
-- ========== CHARGE_BACK ==========
-- 0-99999999999
(84320362390006008, 83864700725799007, 83869193261548004, 'BOLETO', 'CHARGE_BACK_FEE', 1, 0, 99999999999, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 1);



-- ==========【 cb_inter-巴西-PAY_OUT-PIX 】==========
-- ========== TRANSACTION_FEE ==========
-- 0-1000000/1000001-5000000/5000001-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362390007001, 83864700725799008, 83869193261548005, 'PIX', 'TRANSACTION_FEE', 1, 0, 1000001, 0, 50, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390007002, 83864700725799008, 83869193261548005, 'PIX', 'TRANSACTION_FEE', 1, 1000001, 5000001, 0, 30, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390007003, 83864700725799008, 83869193261548005, 'PIX', 'TRANSACTION_FEE', 1, 5000001, 99999999999, 0, 20, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND ==========
-- 0-99999999999
(84320362390007004, 83864700725799008, 83869193261548005, 'PIX', 'REFUND_FEE', 1, 0, 99999999999, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 1),
-- ========== CHARGE_BACK ==========
-- 0-99999999999
(84320362390007005, 83864700725799008, 83869193261548005, 'PIX', 'CHARGE_BACK_FEE', 1, 0, 99999999999, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 1);


-- ==========【 cb_inter-巴西-PAY_OUT-TED 】==========
-- 0-1000000/1000001-5000000/5000001-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362390008001, 83864700725799008, 83869193261548006, 'TED', 'TRANSACTION_FEE', 1, 0, 1000001, 0, 130, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390008002, 83864700725799008, 83869193261548006, 'TED', 'TRANSACTION_FEE', 1, 1000001, 5000001, 0, 110, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362390008003, 83864700725799008, 83869193261548006, 'TED', 'TRANSACTION_FEE', 1, 5000001, 99999999999, 0, 80, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND ==========
-- 0-99999999999
(84320362390008004, 83864700725799008, 83869193261548006, 'TED', 'REFUND_FEE', 1, 0, 99999999999, 0, 500, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'REFUND', 1),
-- ========== CHARGE_BACK ==========
-- 0-99999999999
(84320362390008005, 83864700725799008, 83869193261548006, 'TED', 'CHARGE_BACK_FEE', 1, 0, 99999999999, 0, 5000, 'BRL', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'CHARGE_BACK', 1);

-- 设置创建时间，修改时间
UPDATE `service_accounting_base`.`account_fee_configuration` SET `created_time` = '2022-01-01 00:00:00', `updated_time` = '2022-01-01 00:00:00' WHERE account_id IN ('83864700725799007','83864700725799008');


-- 产品开通 交易量梯度-1
-- calii-巴西-PAY_IN-(SPEI)/calii-巴西-PAY_OUT-(SPEI)
INSERT INTO `service_accounting_base`.`account_product` (`id`, `account_id`, `transaction_type_code`, `product_code`, `monthly_volume_type`, `timezone`, `open_time`) VALUES
(83869193261547540, 83864700725755923, 'PAY_IN', 'SPEI', 1, 'UTC+8', now()),
(83869193261547541, 83864700725755924, 'PAY_OUT', 'SPEI', 1, 'UTC+8', now());


-- 费用配置
-- ==========【 calii-巴西-PAY_IN-SPEI 】==========
-- ========== TRANSACTION_FEE ==========
-- 0-500/500-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362389240001, 83864700725755923, 83869193261547540, 'SPEI', 'TRANSACTION_FEE', 1, 0, 1000001, 0, 350, 'MXN', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362389240002, 83864700725755923, 83869193261547540, 'SPEI', 'TRANSACTION_FEE', 1, 1000001, 5000001, 0, 300, 'MXN', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND CHARGE_BACK 原则上不用配置，没有==========
-- ========== FX_LOSE ==========
-- 0-99999999999
(84320362389240003, 83864700725755923, 83869193261547540, 'SPEI', 'FX_LOSE', 1, 0, 99999999999, 1, 0.02, 'MXN', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== TAX ==========
-- 0-99999999999
(84320362389240004, 83864700725755923, 83869193261547540, 'SPEI', 'TAX', 1, 0, 99999999999, 1, 0.16, 'MXN', 0, 99999999999, 'TRANSACTION', 'FEE', 'SETTLED', 1);

-- ==========【 calii-巴西-PAY_OUT-SPEI 】==========
-- ========== TRANSACTION_FEE ==========
-- 0-500/500-99999999999  金额/交易量梯度-0/1   固定/百分比-0/1
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362389250001, 83864700725755924, 83869193261547541, 'SPEI', 'TRANSACTION_FEE', 1, 0, 1000001, 0, 350, 'MXN', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
(84320362389250002, 83864700725755924, 83869193261547541, 'SPEI', 'TRANSACTION_FEE', 1, 1000001, 5000001, 0, 300, 'MXN', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== REFUND CHARGE_BACK 原则上不用配置，没有==========
-- ========== FX_LOSE ==========
-- 0-99999999999
(84320362389250003, 83864700725755924, 83869193261547541, 'SPEI', 'FX_LOSE', 1, 0, 99999999999, 1, 0.02, 'MXN', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'SETTLED', 1),
-- ========== TAX ==========
-- 0-99999999999
(84320362389250004, 83864700725755924, 83869193261547541, 'SPEI', 'TAX', 1, 0, 99999999999, 1, 0.16, 'MXN', 0, 99999999999, 'TRANSACTION', 'FEE', 'SETTLED', 1);

-- 设置创建时间，修改时间
UPDATE `service_accounting_base`.`account_fee_configuration` SET `created_time` = '2022-01-01 00:00:00', `updated_time` = '2022-01-01 00:00:00' WHERE account_id IN ('83864700725755923','83864700725755924');
