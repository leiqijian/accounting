ALTER TABLE `service_accounting_statement`.`transaction_cost_extra`
    ADD COLUMN `cdi_profit_income_usd` decimal(26, 6) NULL DEFAULT 0 AFTER `extra_fx_usd`,
ADD COLUMN `business_tag` varchar(255) NULL DEFAULT 'DEFAULT'  COMMENT 'Business Tag' AFTER `extra_record_date`;