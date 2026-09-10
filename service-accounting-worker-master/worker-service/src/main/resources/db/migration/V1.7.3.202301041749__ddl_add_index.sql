ALTER TABLE `service_accounting_worker`.`transaction_ratio`
    ADD INDEX `idx_country_code_transaction_type_date`(`country_code`,`transaction_type`, `date`) USING BTREE;