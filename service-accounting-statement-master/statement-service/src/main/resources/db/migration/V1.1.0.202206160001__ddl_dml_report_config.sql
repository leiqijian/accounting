ALTER TABLE `service_accounting_statement`.`report_config`
    ADD COLUMN `upload_path` varchar(255) NULL AFTER `protocol`;

UPDATE `service_accounting_statement`.`report_config` SET `upload_path` = 'kwai/test/' WHERE `id` = 1;
UPDATE `service_accounting_statement`.`report_config` SET `upload_path` = 'didi/test/' WHERE `id` = 2;