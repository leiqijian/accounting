ALTER TABLE `service_accounting_statement`.`report_config`
    ADD COLUMN `merchant_code` varchar(128) NULL AFTER `merchant_id`;