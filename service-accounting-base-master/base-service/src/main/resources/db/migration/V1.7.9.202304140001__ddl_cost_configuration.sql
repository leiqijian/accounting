-- step1：add country_code and transaction_type_code to cost_configuration table
ALTER TABLE `service_accounting_base`.`cost_configuration`
    ADD COLUMN `country_code` varchar(2) NOT NULL COMMENT 'CountryCodeEnum' AFTER `account_id`,
    ADD COLUMN `transaction_type_code` varchar(32) NOT NULL
        COMMENT 'TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS' AFTER `country_code`;

-- step2：supplementary country_code and transaction_type_code data recording
UPDATE `service_accounting_base`.cost_configuration c INNER JOIN `service_accounting_statement`.account a
ON c.account_id = a.id SET c.country_code = a.country_code,c.transaction_type_code = a.transaction_type_code;