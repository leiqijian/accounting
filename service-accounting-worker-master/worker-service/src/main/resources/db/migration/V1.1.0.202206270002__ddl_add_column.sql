ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD COLUMN `transaction_timestamp` int NULL AFTER `transaction_time`;

update `service_accounting_worker`.`task_fee_calculation`
set `transaction_timestamp` = UNIX_TIMESTAMP(`transaction_time`);