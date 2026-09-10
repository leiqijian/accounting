-- 账户开通
-- dukpay-巴西-(PAY_IN/PAY_OUT)
INSERT INTO `service_accounting_statement`.`account` (`id`, `merchant_id`, `trade_merchant_id`, `country_code`,
                                                      `transaction_type_code`, `latest_daily_balance`,
                                                      `sub_total_amount`, `currency`, `source_currency`, `timezone`,
                                                      `timezone_name`, `created_time`, `updated_time`)
VALUES (83864700725755919, 83862785170341885, NULL, 'BR', 'PAY_IN', 0, 0, 'BRL', NULL, 'UTC+8',
        'Beijing, China (GMT+8)', now(), now()),
       (83864700725755920, 83862785170341885, NULL, 'BR', 'PAY_OUT', 0, 0, 'BRL', NULL, 'UTC+8',
        'Beijing, China (GMT+8)', now(), now());