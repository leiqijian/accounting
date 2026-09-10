ALTER TABLE `service_accounting_statement`.`transaction_money`
    ADD COLUMN `submit_time` timestamp NULL COMMENT 'original task_fee_calculation.submit_time UTC+0' AFTER `settlement_currency`;