ALTER TABLE `service_accounting_statement`.`account_daily_bill`
    ADD COLUMN `bill_reconciliation_flag` tinyint(1) NULL DEFAULT 0 COMMENT '0-unreconciled account, 1-reconciled accounts'
    AFTER `del_flag`;