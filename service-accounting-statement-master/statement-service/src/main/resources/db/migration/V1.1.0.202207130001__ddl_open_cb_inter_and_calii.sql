-- 清除数据
DELETE FROM `service_accounting_statement`.`account` WHERE `merchant_id` IN (83862785170341901, 83862785170341886);
DELETE FROM `service_accounting_statement`.`report_config` WHERE `merchant_id` IN (83862785170341901, 83862785170341886);

-- 账户开通
-- cb_inter-巴西-(PAY_IN/PAY_OUT)
INSERT INTO `service_accounting_statement`.`account` (`id`, `merchant_id`, `trade_merchant_id`, `country_code`, `transaction_type_code`, `latest_daily_balance`, `sub_total_amount`, `currency`, `source_currency`, `timezone`, `timezone_name`, `created_time`, `updated_time`) VALUES
    (83864700725799007, 83862785170341901, NULL, 'BR', 'PAY_IN', 0, 0, 'BRL', NULL, 'UTC-3', 'Brazil_City (UTC-3)', now(), now()),
    (83864700725799008, 83862785170341901, NULL, 'BR', 'PAY_OUT', 0, 0, 'BRL', NULL, 'UTC-3', 'Brazil_City (UTC-3)', now(), now());
-- 报表配置
INSERT INTO `service_accounting_statement`.`report_config` (`id`, `merchant_id`, `merchant_code`, `report_strategy`, `file_type`, `compress`, `upload_protocol`, `upload_path`) VALUES
    (7, 83862785170341901, 'cb_inter', 'GENERIC', 'XLSX', 0, 'S3', NULL);

-- 账户开通
-- calii-巴西-(PAY_IN/PAY_OUT)
INSERT INTO `service_accounting_statement`.`account` (`id`, `merchant_id`, `trade_merchant_id`, `country_code`, `transaction_type_code`, `latest_daily_balance`, `sub_total_amount`, `currency`, `source_currency`, `timezone`, `timezone_name`, `created_time`, `updated_time`) VALUES
    (83864700725755923, 83862785170341886, NULL, 'MX', 'PAY_IN', 0, 0, 'MXN', NULL, 'UTC+8', 'Beijing, China (GMT+8)', now(), now()),
    (83864700725755924, 83862785170341886, NULL, 'MX', 'PAY_OUT', 0, 0, 'MXN', NULL, 'UTC+8', 'Beijing, China (GMT+8)', now(), now());
-- 报表配置
INSERT INTO `service_accounting_statement`.`report_config` (`id`, `merchant_id`, `merchant_code`, `report_strategy`, `file_type`, `compress`, `upload_protocol`, `upload_path`) VALUES
    (8, 83862785170341886, 'calii', 'GENERIC', 'XLSX', 0, 'S3', NULL);