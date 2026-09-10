UPDATE `monthly_fee_configuration`
SET `fee_value` = 2.000000
WHERE `account_id` = 83864700725755913
  and `account_product_id` = 83869193261547523
  and `product_code` = 'PIX'
  and `fee_type_code` = 'TRANSACTION_FEE'
  and `active_month` = 202206
  and `fee_value_model` = 0;

Update `service_accounting_base`.`monthly_fee_configuration`
SET `instant_flag`=1
WHERE `account_id` IN (83864700725755919, 83864700725755920);