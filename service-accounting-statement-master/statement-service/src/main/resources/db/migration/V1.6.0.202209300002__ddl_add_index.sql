ALTER TABLE `service_accounting_statement`.`transaction_money`
    DROP INDEX `idx_bill_id`,
    ADD INDEX `idx_account_be_credit_date`(`account_id`, `be_credited_date`) USING BTREE;