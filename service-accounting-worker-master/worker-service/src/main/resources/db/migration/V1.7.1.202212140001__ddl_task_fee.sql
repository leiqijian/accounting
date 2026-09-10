ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD COLUMN `vendor` varchar(255) NULL COMMENT 'vendor' AFTER `trade_transaction_type`;