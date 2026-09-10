ALTER TABLE `service_accounting_statement`.`transaction_money`
    ADD COLUMN `calculation_rule` json NULL AFTER `bill_id`;