ALTER TABLE `service_accounting_statement`.`account_daily_bill`
    ADD COLUMN `daily_extractable_info` json NULL COMMENT 'daily extractable amount info' AFTER `recorded_amount`;