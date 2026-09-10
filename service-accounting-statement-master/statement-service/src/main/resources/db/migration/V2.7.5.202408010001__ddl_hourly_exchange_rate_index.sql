ALTER TABLE `service_accounting_statement`.`hourly_exchange_rate`
    ADD INDEX `idx_exchange_time`(`exchange_time`) USING BTREE;