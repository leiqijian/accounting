ALTER TABLE `service_accounting_statement`.`transaction_money`
    DROP INDEX `idx_transaction_id`,
    DROP INDEX `udx_unique_order`,
    ADD INDEX `idx_unique_id` (`unique_id`) USING BTREE,
    ADD UNIQUE INDEX `udx_unique_order` (`transaction_id`, `direction_type`) USING BTREE;

ALTER TABLE `service_accounting_statement`.`transaction_fee`
    CHANGE COLUMN `monthly_fee_configuration_id` `fee_configuration_id` bigint NOT NULL COMMENT 'FK' AFTER `account_id`,
    DROP INDEX `idx_transaction_time`,
    DROP INDEX `idx_merchant_account_id`,
    ADD INDEX `idx_account_transaction` (`account_id`, `transaction_time`) USING BTREE;

ALTER TABLE `service_accounting_statement`.`transaction_cost`
    DROP INDEX `udx_transaction_id`,
    DROP INDEX `udx_unique_order`,
    ADD INDEX `idx_unique_id`(`unique_id`) USING BTREE,
    ADD UNIQUE INDEX `udx_unique_order`(`transaction_id`, `direction_type`) USING BTREE;