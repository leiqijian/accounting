-- account_fee_configuration
ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    ADD COLUMN `min_volume` decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'Min volume(Limit single transaction amount)' AFTER
    `max_monthly_volume`,
ADD COLUMN `max_volume` decimal(26, 0) NOT NULL DEFAULT 99999999999 COMMENT 'Max volume(Limit single transaction amount)' AFTER `min_volume`;

-- monthly_fee_configuration
ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
    ADD COLUMN `min_volume` decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'Min volume(Limit single transaction amount)' AFTER `active_month`,
ADD COLUMN `max_volume` decimal(26, 0) NOT NULL DEFAULT 99999999999 COMMENT 'Max volume(Limit single transaction amount)' AFTER `min_volume`;