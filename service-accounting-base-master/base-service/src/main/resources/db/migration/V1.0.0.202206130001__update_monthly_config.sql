UPDATE `monthly_fee_configuration`
SET `min_fee_amount` = 14
WHERE `account_id` = 83864700725755911
  AND `account_product_id` = 83869193261547521
  AND `product_code` = 'SPEI'
  AND `fee_type_code` = 'TRANSACTION_FEE'
  AND `active_month` = 202206
  AND `fee_value_model` = 1;