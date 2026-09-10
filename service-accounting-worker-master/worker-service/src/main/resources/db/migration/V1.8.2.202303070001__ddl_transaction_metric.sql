ALTER TABLE `service_accounting_worker`.`transaction_metric`
DROP INDEX `idx_merchant_code_country_date`,
ADD UNIQUE INDEX `udx_unique_data`(`merchant_code`, `country`, `transaction_type`, `date`) USING BTREE;