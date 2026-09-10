ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD COLUMN `submit_timestamp` int NULL AFTER `transaction_timestamp`;

update `service_accounting_worker`.`task_fee_calculation`
set `submit_timestamp` = UNIX_TIMESTAMP(`transaction_time`);

ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD INDEX `idx_transaction_page` (`merchant_code`, `country_code`, `transaction_type_code`, `submit_timestamp`, `del_flag`)
    USING BTREE;

ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    MODIFY COLUMN `submit_timestamp` int NOT NULL AFTER `transaction_timestamp`;