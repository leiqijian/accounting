-- 优化：删除未使用的字段
-- 从梯度费用配置表中删除不再使用的字段
ALTER TABLE `service_accounting_base`.`account_fee_configuration`
DROP COLUMN `fee_settle_model`,
DROP COLUMN `charge_on`;

-- 从月度费用配置表中删除不再使用的字段
ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
DROP COLUMN `fee_settle_model`,
DROP COLUMN `charge_on`;