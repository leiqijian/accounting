ALTER TABLE `service_accounting_base`.`merchant`
    ADD COLUMN `owner` varchar(32) NOT NULL DEFAULT 'APAC' COMMENT 'APAC, SSL, BR, US' AFTER `inner_flag`;