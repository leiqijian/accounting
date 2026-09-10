ALTER TABLE `service_accounting_statement`.`account_balance_snapshot`
    ADD COLUMN `risk_reserve_hold_amount` decimal(26, 0) DEFAULT 0 COMMENT 'risk_reserve_hold_amount' AFTER `pending_amount`;

ALTER TABLE `service_accounting_statement`.`account_balance_snapshot`
    ADD COLUMN `legal_hold_amount` decimal(26, 0) DEFAULT 0 COMMENT 'legal_hold_amount' AFTER `risk_reserve_hold_amount`;