ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD COLUMN `account_name` varchar(255) NULL COMMENT 'payer/payee' AFTER `submit_timestamp`;

ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
DROP INDEX `idx_transaction_page`,
ADD INDEX `idx_transaction_page`(`merchant_code`, `country_code`, `transaction_type_code`, `submit_timestamp`,
    `product_code`, `transaction_status`, `direction_type`, `account_name`, `del_flag`) USING BTREE;