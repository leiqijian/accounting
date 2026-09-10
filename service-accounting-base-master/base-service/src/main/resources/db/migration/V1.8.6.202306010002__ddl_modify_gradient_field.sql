-- 修改费用配置 最小收费字段，最大收费字段
ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    MODIFY COLUMN `min_fee_amount` decimal(26) NOT NULL AFTER `currency`,
    MODIFY COLUMN `max_fee_amount` decimal(26) NOT NULL AFTER `min_fee_amount`;