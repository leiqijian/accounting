ALTER TABLE `service_accounting_statement`.`account_transfer_config`
    ADD COLUMN `timer_cron` varchar(64) NULL DEFAULT '' COMMENT 'Customize timer utc+0 cron expression' AFTER `payout_account_id`;