-- 修正所有的 `min_monthly_volume` & `max_monthly_volume`
UPDATE `service_accounting_base`.`account_fee_configuration`
SET `min_monthly_volume` = min_monthly_volume * 100,
    `max_monthly_volume` = max_monthly_volume * 100
WHERE
    RIGHT ( `max_monthly_volume`, 2 ) NOT IN ( 00, 99 );


UPDATE `service_accounting_base`.`account_fee_configuration`
SET `min_monthly_volume` = min_monthly_volume * 100,
    `max_monthly_volume` = max_monthly_volume * 100
WHERE  (RIGHT ( `max_monthly_volume`, 2 )= 99 && RIGHT ( `min_monthly_volume`, 2 ) NOT IN ( 00, 99 ));
