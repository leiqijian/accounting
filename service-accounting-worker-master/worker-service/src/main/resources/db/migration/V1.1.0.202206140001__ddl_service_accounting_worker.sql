ALTER TABLE `service_accounting_worker`.`task_log_fee_calculation`
    CHANGE COLUMN `execution_log` `request_data` mediumtext NULL AFTER `task_result`;

ALTER TABLE `service_accounting_worker`.`task_log_fee_calculation`
    ADD COLUMN `response_data` mediumtext NULL AFTER `request_data`;