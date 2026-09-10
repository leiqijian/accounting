ALTER TABLE `service_accounting_statement`.`account_daily_report`
    MODIFY COLUMN `file_name` varchar(255) NULL DEFAULT '' COMMENT 'file name`' AFTER `total_count`,
    MODIFY COLUMN `file_path` varchar(255) NULL DEFAULT '' COMMENT 'file path' AFTER `file_name`;