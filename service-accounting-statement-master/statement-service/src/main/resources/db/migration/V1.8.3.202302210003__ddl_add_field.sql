ALTER TABLE `service_accounting_statement`.`transaction_cost`
DROP INDEX `udx_transaction_id`,
ADD INDEX `udx_transaction_id`(`transaction_id`) USING BTREE;