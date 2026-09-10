ALTER TABLE `service_accounting_statement`.`account_daily_bill`
    ADD COLUMN `recorded_amount` decimal(26, 0) NULL DEFAULT 0 COMMENT 'daily transaction amount recorded' AFTER `extractable_balance`;

UPDATE service_accounting_statement.account a
    LEFT JOIN account_daily_bill b ON (a.id = b.account_id)
SET a.extractable_balance = b.end_balance
WHERE a.transaction_type_code = 'PAY_IN'
  AND b.bill_date = '2022-07-27';
