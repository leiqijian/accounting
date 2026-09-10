ALTER TABLE `service_accounting_statement`.`transaction_money`
    DROP INDEX `udx_transaction_id`,
    DROP INDEX `udx_unique_id`,
    ADD INDEX `idx_transaction_id` (`transaction_id`) USING BTREE,
    ADD UNIQUE INDEX `udx_unique_order` (`unique_id`, `direction_type`) USING BTREE;

ALTER TABLE `service_accounting_statement`.`transaction_cost`
    ADD COLUMN `direction_type` varchar(32) NOT NULL COMMENT 'DirectionTypeEnum: SETTLED/REFUND/CHARGE_BACK/REJECTED' AFTER `transaction_type_code`,
    DROP INDEX `udx_unique_id`,
    ADD UNIQUE INDEX `udx_unique_order` (`unique_id`, `direction_type`) USING BTREE;