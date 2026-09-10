ALTER TABLE `service_accounting_base`.`payment_config`
    MODIFY COLUMN `status` tinyint NULL DEFAULT 1 COMMENT '0-disable, 1-enable' AFTER `version`,
    ADD COLUMN `mock_switch` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '0-OFF; 1-ON' AFTER `status`;