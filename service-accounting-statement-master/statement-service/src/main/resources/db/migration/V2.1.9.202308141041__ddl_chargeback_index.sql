ALTER TABLE `service_accounting_statement`.`transaction_charge_back_order`
DROP INDEX `idx_account_created_time`,
ADD INDEX `idx_account_chargeback_time`(`account_id`, `chargeback_time`) USING BTREE;