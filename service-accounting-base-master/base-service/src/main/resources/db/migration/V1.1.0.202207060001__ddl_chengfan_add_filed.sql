-- 修改乘帆百分比收费为BRL，与Account Currency保持一致
-- 83864700725755916 乘帆-巴西-PAY_IN
-- 83864700725755917 乘帆-巴西-PAY_OUT
-- fee_value_model 0-固定值 1-百分比
UPDATE `service_accounting_base`.`account_fee_configuration` SET `currency` = 'BRL' WHERE `account_id` IN (83864700725755916, 83864700725755917) AND `fee_value_model` = '1';
UPDATE `service_accounting_base`.`monthly_fee_configuration` SET `currency` = 'BRL' WHERE `account_id` IN (83864700725755916, 83864700725755917) AND `fee_value_model` = '1';

-- 新增一个列 fee_settle_model(百分比收费模式 0-转汇前 1-转汇后)
-- enabled when fee_value_model=1,percentage charging mode, 0-before transfer 1-after transfer
ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    ADD COLUMN `fee_settle_model` tinyint UNSIGNED NOT NULL
    DEFAULT 0 COMMENT 'enabled when fee_value_model=1,percentage charging mode, 0-before transfer 1-after transfer'
    AFTER `fee_value_model`;
ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
    ADD COLUMN `fee_settle_model` tinyint UNSIGNED NOT NULL
    DEFAULT 0 COMMENT 'enabled when fee_value_model=1,percentage charging mode, 0-before transfer 1-after transfer'
    AFTER `fee_value_model`;
