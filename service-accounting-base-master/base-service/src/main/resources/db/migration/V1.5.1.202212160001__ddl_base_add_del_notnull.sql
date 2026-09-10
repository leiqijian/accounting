ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal,1-delete' AFTER `version`;

ALTER TABLE `service_accounting_base`.`merchant`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'delFlag: 0-normal, 1-delete' AFTER `version`;

ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal,1-delete' AFTER `version`;

ALTER TABLE `service_accounting_base`.`payment_config`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal, 1-delete' AFTER `mock_switch`;