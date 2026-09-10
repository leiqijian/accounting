-- 修改费用配置 梯度字段
ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    MODIFY COLUMN `min_monthly_volume` decimal(26) NOT NULL COMMENT 'Min monthly volume' AFTER `monthly_volume_type`,
    MODIFY COLUMN `max_monthly_volume` decimal(26) NOT NULL COMMENT 'Max monthly volume' AFTER `min_monthly_volume`;