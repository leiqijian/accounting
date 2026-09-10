-- ==================================statement======================================
-- 账户开通
-- liquido-巴西-(PAY_IN/PAY_OUT/MARKET_PLACE)
INSERT INTO `service_accounting_statement`.`account` (`id`, `merchant_id`, `trade_merchant_id`, `country_code`, `transaction_type_code`, `latest_daily_balance`, `sub_total_amount`, `currency`, `source_currency`, `timezone`, `timezone_name`, `created_time`, `updated_time`) VALUES
(83864700725799001, 83862785170341900, NULL, 'BR', 'PAY_IN', 0, 0, 'USD', NULL, 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
(83864700725799002, 83862785170341900, NULL, 'BR', 'PAY_OUT', 0, 0, 'USD', NULL, 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
(83864700725799003, 83862785170341900, NULL, 'BR', 'MARKET_PLACE_ORDERS', 0, 0, 'USD', NULL, 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
-- liquido-墨西哥-(PAY_IN/PAY_OUT/MARKET_PLACE)
(83864700725799004, 83862785170341900, NULL, 'MX', 'PAY_IN', 0, 0, 'USD', NULL, 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
(83864700725799005, 83862785170341900, NULL, 'MX', 'PAY_OUT', 0, 0, 'USD', NULL, 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
(83864700725799006, 83862785170341900, NULL, 'MX', 'MARKET_PLACE_ORDERS', 0, 0, 'USD', NULL, 'UTC+8', 'Beijing, China (GMT+8)', now(), now());