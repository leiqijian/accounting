UPDATE `service_accounting_base`.`monthly_fee_configuration`
 SET `direction_type` = 'REFUND' WHERE `fee_type_code` = 'REFUND_FEE';

UPDATE `service_accounting_base`.`account_fee_configuration`
 SET `direction_type` = 'REFUND' WHERE `fee_type_code` = 'REFUND_FEE';