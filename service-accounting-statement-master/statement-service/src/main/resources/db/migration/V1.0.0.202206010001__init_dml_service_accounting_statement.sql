-- //========账户开通========//
INSERT INTO `service_accounting_statement`.`account` (`id`, `merchant_id`, `trade_merchant_id`, `country_code`, `transaction_type_code`, `latest_daily_balance`, `sub_total_amount`, `currency`, `source_currency`, `timezone`, `timezone_name`, `created_time`, `updated_time`) VALUES 
-- 快手-墨西哥-(PAY_OUT/MARKET_PLACE_ORDERS)
(83864700725755911, 83862785170341881, '60774784c5a67d7b5a448fc2', 'MX', 'PAY_OUT', -116753, -294684, 'USD', 'MXN', 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
(83864700725755912, 83862785170341881, '60774784c5a67d7b5a448fb1', 'MX', 'MARKET_PLACE_ORDERS', 0, -115, 'USD', 'MXN', 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
-- 快手-巴西-(PAY_OUT)
(83864700725755913, 83862785170341881, '60774784c5a67d7b5a448fe8', 'BR', 'PAY_OUT', -5728899, -21159305, 'USD', 'BRL', 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
-- 滴滴-墨西哥-(PAY_IN/PAY_OUT) 冬夏令时问题UTC-6
(83864700725755914, 83862785170341882, NULL, 'MX', 'PAY_IN', 0, 726, 'MXN', NULL, 'UTC-5', 'Mexico_City (UTC-5)', now(), now()),
(83864700725755915, 83862785170341882, NULL, 'MX', 'PAY_OUT', 0, 0, 'MXN', NULL, 'UTC-5', 'Mexico_City (UTC-5)', now(), now()),
-- 乘帆-巴西-(PAY_IN/PAY_OUT)
(83864700725755916, 83862785170341883, NULL, 'BR', 'PAY_IN', 0, 475700, 'BRL', NULL, 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
(83864700725755917, 83862785170341883, NULL, 'BR', 'PAY_OUT', 0, 0, 'BRL', NULL, 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
-- 万博-巴西-(PAY_IN)
(83864700725755918, 83862785170341884, NULL, 'BR', 'PAY_IN', 0, 0, 'USD', 'BRL', 'UTC+8', 'Beijing, China (GMT+8)', now(), now());

-- 设置创建时间，修改时间
UPDATE `service_accounting_statement`.`account` SET `created_time` = '2022-01-01 00:00:00', `updated_time` = '2022-05-01 00:00:00';