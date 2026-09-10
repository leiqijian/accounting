ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD COLUMN `event_timestamp` int NOT NULL COMMENT 'event timestamp of event time (second)'
    AFTER `event_time`;

update `service_accounting_worker`.`task_fee_calculation`
set `event_timestamp` = UNIX_TIMESTAMP(`event_time`);

ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    ADD INDEX `idx_event_timestamp` (`event_timestamp`)
    USING BTREE;