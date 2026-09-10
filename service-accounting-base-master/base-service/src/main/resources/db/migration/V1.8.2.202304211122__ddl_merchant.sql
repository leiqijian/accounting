ALTER TABLE `service_accounting_base`.`merchant`
    ADD COLUMN `balance_alarm_flag` tinyint(1) NOT NULL COMMENT 'payout、marketplace balance alarm switch' AFTER `inner_flag`,
ADD COLUMN `balance_alarm_email` json NULL COMMENT 'Email address that sends alarms when the balance is insufficient' AFTER `balance_alarm_flag`;