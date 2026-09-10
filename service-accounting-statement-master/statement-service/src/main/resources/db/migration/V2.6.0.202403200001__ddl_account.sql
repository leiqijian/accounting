-- 删除account表source_currency字段
ALTER TABLE `service_accounting_statement`.`account`
DROP
COLUMN `source_currency`;