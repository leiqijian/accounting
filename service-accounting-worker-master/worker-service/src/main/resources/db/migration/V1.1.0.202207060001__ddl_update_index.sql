ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
DROP INDEX `idx_transaction_page`,
ADD INDEX `idx_transaction_page`(`merchant_code`, `country_code`, `transaction_type_code`, `submit_timestamp`,
    `product_code`, `transaction_status`, `direction_type`, `del_flag`) USING BTREE;