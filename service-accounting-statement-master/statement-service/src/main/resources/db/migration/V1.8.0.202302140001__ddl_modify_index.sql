ALTER TABLE `service_accounting_statement`.`transaction_money`
DROP INDEX `idx_merchant_account_id`,
DROP INDEX `idx_transaction_time`,
ADD INDEX `idx_merchant_account_id`(`account_id`, `transaction_time`) USING BTREE;