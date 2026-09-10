ALTER TABLE `service_accounting_base`.`account_product`
    MODIFY COLUMN `monthly_volume_group` varchar(1024)
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
    COMMENT 'Account group statistics' AFTER `monthly_volume_type`;