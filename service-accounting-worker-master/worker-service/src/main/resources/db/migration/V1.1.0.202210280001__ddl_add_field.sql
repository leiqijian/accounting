ALTER TABLE `service_accounting_worker`.`task_holding_monitor`
    ADD COLUMN `account_id` bigint UNSIGNED NOT NULL COMMENT 'account Id' AFTER `monthly`;

ALTER TABLE `service_accounting_worker`.`task_holding_monitor`
    DROP INDEX `udx_monthly_document_id`,
    ADD UNIQUE INDEX `udx_monthly_document_id`(`monthly`, `account_id`, `document_id`) USING BTREE;