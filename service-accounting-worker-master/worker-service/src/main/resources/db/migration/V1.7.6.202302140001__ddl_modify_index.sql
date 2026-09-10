ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
DROP INDEX `idx_transaction_query`,
ADD INDEX `idx_charge_back_page`(`merchant_code`, `country_code`, `transaction_type_code`, `transaction_timestamp`, `product_code`, `trade_transfer_status`) USING BTREE;