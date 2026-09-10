ALTER TABLE `service_accounting_statement`.`biz_transfer_out`
    MODIFY COLUMN `amount` decimal(26, 0) NULL DEFAULT 0 COMMENT 'save integer type, default to penny' AFTER `payment_action`,
    MODIFY COLUMN `fx_rate` decimal(26, 6) NULL DEFAULT 1 AFTER `currency`,
    MODIFY COLUMN `settlement_amount` decimal(26, 0) NULL DEFAULT 0 AFTER `fx_rate`,
    MODIFY COLUMN `fee` decimal(26, 0) NULL DEFAULT 0 AFTER `settlement_currency`,
    MODIFY COLUMN `tax` decimal(26, 0) NULL DEFAULT 0 AFTER `fee`;

ALTER TABLE `service_accounting_statement`.`biz_refund`
    MODIFY COLUMN `amount` decimal(26, 0) NULL DEFAULT 0 COMMENT 'save integer type, default to penny' AFTER `transaction_type_code`,
    MODIFY COLUMN `fx_rate` decimal(26, 6) NULL DEFAULT 1 AFTER `currency`,
    MODIFY COLUMN `settlement_amount` decimal(26, 0) NULL DEFAULT 0 AFTER `fx_rate`,
    MODIFY COLUMN `fee` decimal(26, 0) NULL DEFAULT 0 AFTER `settlement_currency`,
    MODIFY COLUMN `tax` decimal(26, 0) NULL DEFAULT 0 AFTER `fee`;

ALTER TABLE `service_accounting_statement`.`biz_topup`
    MODIFY COLUMN `amount` decimal(26, 0) NULL DEFAULT 0 COMMENT 'save integer type, default to penny' AFTER `transaction_type_code`,
    MODIFY COLUMN `fx_rate` decimal(26, 6) NULL DEFAULT 1 AFTER `currency`,
    MODIFY COLUMN `settlement_amount` decimal(26, 0) NULL DEFAULT 0 AFTER `fx_rate`,
    MODIFY COLUMN `fee` decimal(26, 0) NULL DEFAULT 0 AFTER `settlement_currency`,
    MODIFY COLUMN `tax` decimal(26, 0) NULL DEFAULT 0 AFTER `fee`;