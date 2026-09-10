-- account 表新增 account_config_id 字段
ALTER TABLE `service_accounting_statement`.`account`
    ADD COLUMN `account_config_id` bigint NOT NULL COMMENT 'fk' AFTER `merchant_id`;

-- account_config 表新增 exchange_rate_config 字段
ALTER TABLE `service_accounting_statement`.`account_config`
    ADD COLUMN `exchange_rate_config` json NOT NULL AFTER `account_id`;

-- 更新 account_config_id 字段数据
UPDATE account AS a
    INNER JOIN ( SELECT id, account_id FROM account_config ) AS ac
ON a.id = ac.account_id
    SET a.account_config_id = ac.id;

-- 删除account_config表的account_id字段，单向关联
ALTER TABLE `service_accounting_statement`.`account_config`
DROP
COLUMN `account_id`;