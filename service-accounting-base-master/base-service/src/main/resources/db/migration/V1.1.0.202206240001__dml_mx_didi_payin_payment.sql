-- ========== 滴滴-墨西哥-PAY_IN-SPEI 自动打款 BIZ_TRANSFER_OUT ==========
INSERT INTO `service_accounting_base`.`account_fee_configuration` (`id`, `account_id`, `account_product_id`, `product_code`, `fee_type_code`, `monthly_volume_type`, `min_monthly_volume`, `max_monthly_volume`, `fee_value_model`, `fee_value`, `currency`, `min_fee_amount`, `max_fee_amount`, `charge_on`, `fee_on`, `direction_type`, `instant_flag`) VALUES
(84320362389175005, 83864700725755915, 83869193261547526, 'SPEI', 'TRANSACTION_FEE', 1, 0, 25001, 0, 500, 'MXN', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'AUTO_TRANSFER_OUT', 1),
(84320362389175006, 83864700725755915, 83869193261547526, 'SPEI', 'TRANSACTION_FEE', 1, 25001, 50001, 0, 300, 'MXN', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'AUTO_TRANSFER_OUT', 1),
(84320362389175007, 83864700725755915, 83869193261547526, 'SPEI', 'TRANSACTION_FEE', 1, 50001, 999999999, 0, 250, 'MXN', 0, 99999999999, 'TRANSACTION', 'AMOUNT', 'AUTO_TRANSFER_OUT', 1),
-- ========== TAX ==========
-- 0-99999999999
(84320362389175008, 83864700725755915, 83869193261547526, 'SPEI', 'TAX', 1, 0, 99999999999, 1, 0.16, 'MXN', 0, 99999999999, 'TRANSACTION', 'FEE', 'AUTO_TRANSFER_OUT', 1);
