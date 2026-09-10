-- ========== account_product ==========
-- 将产品 CREDIT_CARD 修改为 CARD
UPDATE `service_accounting_base`.`account_product` SET  `product_code` = 'CARD' WHERE `product_code` = 'CREDIT_CARD';
-- 删除 DEBIT_CARD 的产品
DELETE FROM `service_accounting_base`.`account_product` WHERE `product_code` = 'DEBIT_CARD';
-- 删除 DEBIT_CARD 费用配置
DELETE FROM `service_accounting_base`.`account_fee_configuration` WHERE `product_code` = 'DEBIT_CARD';
-- 删除 DEBIT_CARD 月度费用配置
DELETE FROM `service_accounting_base`.`monthly_fee_configuration` WHERE `product_code` = 'DEBIT_CARD';


-- ========== account_fee_configuration ==========
-- 处理'DEFAULT'数据
UPDATE `service_accounting_base`.`account_fee_configuration` SET `calculation_rule` = '{}'
WHERE `calculation_rule` = 'DEFAULT';
-- 处理有卡组，没有分期的数据
UPDATE `service_accounting_base`.`account_fee_configuration` SET `calculation_rule` =
    CONCAT('{\"cardBrand\":\"', `calculation_rule`, '\"}')
WHERE `calculation_rule` NOT LIKE '%:%' AND `calculation_rule` <> '{}';
-- 处理包含分期的数据
UPDATE `service_accounting_base`.`account_fee_configuration`
SET `calculation_rule` =
        CONCAT(
                '{"cardBrand":"',
                SUBSTRING_INDEX(`calculation_rule`, ':', 1),
                '","cardInstallments":"',
                SUBSTRING_INDEX(`calculation_rule`, ':', -1),
                '"}'
            )
WHERE `calculation_rule` NOT LIKE '{%' AND `calculation_rule` LIKE '%INSTALLMENT%';

-- 修改数据库索引 和 字段“计算属性”为json类型
ALTER TABLE `service_accounting_base`.`account_fee_configuration`
    MODIFY COLUMN `calculation_rule` json COMMENT 'Fee calculation rules' AFTER `product_code`,
DROP INDEX `udx_product_fee_type`,
ADD INDEX `idx_account_product`(`account_id`, `account_product_id`);

-- 处理空数据
UPDATE `service_accounting_base`.`account_fee_configuration`
SET `calculation_rule` = NULL WHERE JSON_VALUE(`calculation_rule`, '$') = '{}';

-- 处理分期数据
UPDATE `service_accounting_base`.`account_fee_configuration`
SET `calculation_rule` =
        CASE
            WHEN JSON_UNQUOTE(JSON_EXTRACT(`calculation_rule`, '$.cardInstallments')) = '2-6' THEN
                JSON_SET(`calculation_rule`, '$.cardInstallments', '2,3,4,5,6')
            WHEN JSON_UNQUOTE(JSON_EXTRACT(`calculation_rule`, '$.cardInstallments')) = '7-12' THEN
                JSON_SET(`calculation_rule`, '$.cardInstallments', '7,8,9,10,11,12')
            ELSE
                `calculation_rule`
            END
WHERE JSON_UNQUOTE(JSON_EXTRACT(`calculation_rule`, '$.cardInstallments')) IN ('2-6', '7-12');

UPDATE `service_accounting_base`.`account_fee_configuration` SET `product_code` = 'CARD' WHERE `product_code` = 'CREDIT_CARD';


-- ========== monthly_fee_configuration ==========
-- 处理'DEFAULT'数据
UPDATE `service_accounting_base`.`monthly_fee_configuration` SET `calculation_rule` = '{}'
WHERE `calculation_rule` = 'DEFAULT';
-- 处理有卡组，没有分期的数据
UPDATE `service_accounting_base`.`monthly_fee_configuration` SET `calculation_rule` =
    CONCAT('{\"cardBrand\":\"', `calculation_rule`, '\"}')
WHERE `calculation_rule` NOT LIKE '%:%' AND `calculation_rule` <> '{}';
-- 处理包含分期的数据
UPDATE `service_accounting_base`.`monthly_fee_configuration`
SET `calculation_rule` =
        CONCAT(
                '{"cardBrand":"',
                SUBSTRING_INDEX(`calculation_rule`, ':', 1),
                '","cardInstallments":"',
                SUBSTRING_INDEX(`calculation_rule`, ':', -1),
                '"}'
            )
WHERE `calculation_rule` NOT LIKE '{%' AND `calculation_rule` LIKE '%INSTALLMENT%';

-- 修改数据库索引 和 字段“计算属性”为json类型
ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
    MODIFY COLUMN `calculation_rule` json COMMENT 'Fee calculation rules' AFTER `product_code`,
DROP INDEX `udx_month_fee_config`,
ADD INDEX `idx_account_product_month`(`account_id`, `account_product_id`, `active_month`);

-- 处理空数据
UPDATE `service_accounting_base`.`monthly_fee_configuration`
SET `calculation_rule` = NULL WHERE JSON_VALUE(`calculation_rule`, '$') = '{}';

-- 处理包含分期的数据
UPDATE `service_accounting_base`.`monthly_fee_configuration`
SET `calculation_rule` =
        CASE
            WHEN JSON_UNQUOTE(JSON_EXTRACT(`calculation_rule`, '$.cardInstallments')) = '2-6' THEN
                JSON_SET(`calculation_rule`, '$.cardInstallments', '2,3,4,5,6')
            WHEN JSON_UNQUOTE(JSON_EXTRACT(`calculation_rule`, '$.cardInstallments')) = '7-12' THEN
                JSON_SET(`calculation_rule`, '$.cardInstallments', '7,8,9,10,11,12')
            ELSE
                `calculation_rule`
            END
WHERE JSON_UNQUOTE(JSON_EXTRACT(`calculation_rule`, '$.cardInstallments')) IN ('2-6', '7-12');

UPDATE `service_accounting_base`.`monthly_fee_configuration` SET `product_code` = 'CARD' WHERE `product_code` = 'CREDIT_CARD';