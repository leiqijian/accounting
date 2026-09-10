-- 月度费用配置表引用梯度费用配置的ID来表示执行的梯度
ALTER TABLE `service_accounting_base`.`monthly_fee_configuration`
    ADD COLUMN `account_fee_configuration_id` bigint NOT NULL COMMENT 'Identify the gradient of execution' AFTER `account_product_id`;


-- 添加产品与费用配置的关联关系表，表示每月指定时间段执行的费用配置版本
DROP TABLE IF EXISTS `account_product_version`;
CREATE TABLE `account_product_version` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `account_id` bigint NOT NULL COMMENT 'fk ref: account:id',
   `account_product_id` bigint NOT NULL COMMENT 'fk ref: account_product.id',
   `active_month` int NOT NULL COMMENT 'yyyyMM',
   `start_date` date NOT NULL COMMENT 'start_date format:yyyyMMdd',
   `end_date` date NOT NULL COMMENT 'end_date format:yyyyMMdd',
   `account_fee_version` int unsigned NOT NULL COMMENT 'account fee configuration version',
   `monthly_fee_version` int unsigned NOT NULL COMMENT 'monthly fee configuration version',
   `monthly_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-not generated,1-generate',
   `created_time` datetime DEFAULT NULL,
   `updated_time` datetime DEFAULT NULL,
   `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-norma,1-delete',
   `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'remark',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE KEY `uk_config` (`account_product_id`,`active_month`,`start_date`,`end_date`,`account_fee_version`,`monthly_fee_version`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=472 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;