ALTER TABLE `service_accounting_statement`.`account_statement`
ADD COLUMN `transaction_timestamp` int UNSIGNED NULL DEFAULT 0 AFTER `transaction_time`,
DROP INDEX `idx_transaction_time`,
ADD INDEX `idx_transaction_timestamp`(`transaction_timestamp`) USING BTREE;

UPDATE `service_accounting_statement`.`account_statement` SET `transaction_timestamp` = UNIX_TIMESTAMP(`transaction_time`);