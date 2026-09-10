-- 83869193261547535 dukpay-巴西-PAY_IN-PIX
-- 83869193261547536 dukpay-巴西-PAY_IOUT-PIX
UPDATE `service_accounting_base`.`account_product` SET  `monthly_volume_type` = 1
    WHERE `id` IN (83869193261547535, 83869193261547536);
UPDATE `service_accounting_base`.`account_fee_configuration` SET `monthly_volume_type` = 1
    WHERE `account_product_id` IN (83869193261547535, 83869193261547536);
-- 修改7月月度配置
UPDATE `service_accounting_base`.`monthly_fee_configuration` SET `fee_value` = 50.000000
    WHERE `account_product_id` IN (83869193261547535, 83869193261547536)
    AND `fee_type_code` = 'TRANSACTION_FEE' AND `active_month` = '202207';