-- ==================================statement======================================
-- 账户开通
-- 万博-巴西-(PAY_IN/PAY_OUT)
DELETE
FROM `service_accounting_statement`.`account`
WHERE `id` in (83864700725755918, 83864700725755921, 83864700725755922);
INSERT INTO `service_accounting_statement`.`account` (`id`, `merchant_id`, `trade_merchant_id`, `country_code`,
                                                      `transaction_type_code`, `latest_daily_balance`,
                                                      `sub_total_amount`, `currency`, `source_currency`, `timezone`,
                                                      `timezone_name`, `created_time`, `updated_time`)
VALUES (83864700725755921, 83862785170341884, NULL, 'BR', 'PAY_IN', 0, 0, 'USD', 'BRL', 'UTC+8',
        'Beijing, China (GMT+8)', now(), now()),
       (83864700725755922, 83862785170341884, NULL, 'BR', 'PAY_OUT', 0, 0, 'USD', 'BRL', 'UTC+8',
        'Beijing, China (GMT+8)', now(), now());

-- 商户报表下载配置
UPDATE `service_accounting_statement`.`report_config`
SET `merchant_code`   = 'wan_bo',
    `file_type`       = 'XLSX',
    `compress`        = 0,
    `upload_protocol` = 'S3',
    `upload_path`     = NULL
WHERE `merchant_id` = 83862785170341884;



