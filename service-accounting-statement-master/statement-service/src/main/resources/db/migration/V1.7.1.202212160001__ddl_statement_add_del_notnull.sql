ALTER TABLE `service_accounting_statement`.`account`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete' AFTER `version`;

ALTER TABLE `service_accounting_statement`.`account_daily_bill`
    MODIFY COLUMN `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete' AFTER `version`;

ALTER TABLE `service_accounting_statement`.`account_statement`
    MODIFY COLUMN `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete' AFTER `version`;

ALTER TABLE `service_accounting_statement`.`biz_refund`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete' AFTER `version`;

ALTER TABLE `service_accounting_statement`.`biz_topup`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete' AFTER `bill_id`;

ALTER TABLE `service_accounting_statement`.`biz_transfer_out`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete' AFTER `bill_id`;

ALTER TABLE `service_accounting_statement`.`daily_exchange_rate`
    MODIFY COLUMN `del_flag` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '0-normal,1-delete' AFTER `version`;

ALTER TABLE `service_accounting_statement`.`transaction_fee`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete' AFTER `bill_id`;

ALTER TABLE `service_accounting_statement`.`transaction_money`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete' AFTER `bill_id`;

ALTER TABLE `service_accounting_statement`.`transaction_payout`
    MODIFY COLUMN `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete' AFTER `version`;