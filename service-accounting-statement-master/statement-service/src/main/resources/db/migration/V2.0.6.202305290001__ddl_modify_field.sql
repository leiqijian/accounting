ALTER TABLE `service_accounting_statement`.`account_config`
    CHANGE COLUMN `balance_alarm_config` `config_data` json NOT NULL AFTER `account_id`;

UPDATE service_accounting_statement.account_config
SET config_data = CONCAT('{"balanceAlarm":', config_data, '}');


ALTER TABLE `service_accounting_statement`.`account_daily_bill`
    ADD COLUMN `calculate_fee`  decimal(26, 6) NULL DEFAULT 0 COMMENT 'actuality calculate amount, unit: cent(keep 6 decimal places)' AFTER `transaction_amount_usd`,
    ADD COLUMN `calculate_tax`  decimal(26, 6) NULL DEFAULT 0 COMMENT 'actuality calculate amount, unit: cent(keep 6 decimal places)' AFTER `calculate_fee`,
    MODIFY COLUMN `transaction_fee` decimal(26, 0) NULL DEFAULT 0 COMMENT 'settled amount, unit: cent(keep 0 decimal places)' AFTER `calculate_tax`,
    MODIFY COLUMN `transaction_tax` decimal(26, 0) NULL DEFAULT 0 COMMENT 'settled amount, unit: cent(keep 0 decimal places)' AFTER `transaction_fee`,
    ADD COLUMN `calculate_fee2` decimal(26, 6) NULL DEFAULT 0 COMMENT 'actuality calculate amount, unit: cent(keep 6 decimal places)' AFTER `transaction_fee_currency`,
    ADD COLUMN `calculate_tax2` decimal(26, 6) NULL DEFAULT 0 COMMENT 'actuality calculate amount, unit: cent(keep 6 decimal places)' AFTER `calculate_fee2`,
    MODIFY COLUMN `transaction_fee2` decimal(26, 0) NULL DEFAULT 0 COMMENT 'settled amount, unit: cent(keep 0 decimal places)' AFTER `calculate_tax2`,
    MODIFY COLUMN `transaction_tax2` decimal(26, 0) NULL DEFAULT 0 COMMENT 'settled amount, unit: cent(keep 0 decimal places)' AFTER `transaction_fee2`;