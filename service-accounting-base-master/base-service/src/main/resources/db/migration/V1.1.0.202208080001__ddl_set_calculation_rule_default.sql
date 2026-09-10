-- 设置计算属性默认值为 'DEFAULT'
UPDATE  `service_accounting_base`.`account_fee_configuration`  SET `calculation_rule` = 'DEFAULT' WHERE `calculation_rule` IS NULL;
ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    MODIFY COLUMN `calculation_rule` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DEFAULT' COMMENT 'Fee calculation rules' AFTER `product_code`;

-- 月度配置配置同步修改
UPDATE  `service_accounting_base`.`monthly_fee_configuration`  SET `calculation_rule` = 'DEFAULT' WHERE `calculation_rule` IS NULL;
ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
    MODIFY COLUMN `calculation_rule` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DEFAULT' COMMENT 'Fee calculation rules' AFTER `product_code`;