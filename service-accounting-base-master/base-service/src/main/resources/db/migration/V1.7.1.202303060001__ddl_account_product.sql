ALTER TABLE `service_accounting_base`.`account_product`
    ADD COLUMN `monthly_volume_group` varchar(255) NOT NULL COMMENT 'Account group statistics' AFTER `monthly_volume_type`;