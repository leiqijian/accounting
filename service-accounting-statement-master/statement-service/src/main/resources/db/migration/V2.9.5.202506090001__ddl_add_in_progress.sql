ALTER TABLE `service_accounting_statement`.`transaction_in_progress`
ADD COLUMN `fee` decimal(26, 2) NOT NULL DEFAULT 0 COMMENT 'pre calculation fee, currency = account currency' AFTER `currency`,
ADD COLUMN `tax` decimal(26, 2) NOT NULL DEFAULT 0 COMMENT 'pre calculation tax, currency = account currency' AFTER `fee`,
ADD COLUMN `net_amount` decimal(26, 2) NOT NULL DEFAULT 0 COMMENT 'pre calculation net amount, currency = account currency, net amount= amount/rate + fee + tax' AFTER `tax`;

ALTER TABLE `service_accounting_statement`.`account_in_progress`
ADD COLUMN `fee` decimal(26, 2) NOT NULL DEFAULT 0 COMMENT 'pre calculation fee, currency = account currency' AFTER `in_progress_currency`,
ADD COLUMN `tax` decimal(26, 2) NOT NULL DEFAULT 0 COMMENT 'pre calculation tax, currency = account currency' AFTER `fee`,
ADD COLUMN `net_amount` decimal(26, 2) NOT NULL DEFAULT 0 COMMENT 'pre calculation net amount, currency = account currency, net amount= amount/rate + fee + tax' AFTER `tax`;

ALTER TABLE `service_accounting_statement`.`transaction_in_progress`
ADD INDEX `idx_account_status_created_time`(`account_id`,`status`,`transaction_status`,`created_time`) USING BTREE;