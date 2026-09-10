ALTER TABLE `service_accounting_statement`.`transaction_biz`
    ADD COLUMN `operate_source` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT 'operate source 0: OFFLINE, 1:ONLINE' AFTER `payment_channel`;