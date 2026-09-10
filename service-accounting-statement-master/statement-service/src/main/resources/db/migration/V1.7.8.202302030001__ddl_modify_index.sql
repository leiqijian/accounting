ALTER TABLE `service_accounting_statement`.`account`
    DROP INDEX `idx_merchant_id`,
    ADD INDEX `idx_merchant_account` (`merchant_id`, `transaction_type_code`) USING BTREE,
    ADD UNIQUE INDEX `udx_merchant_country_account` (`merchant_id`, `country_code`, `transaction_type_code`, `del_flag`) USING BTREE;