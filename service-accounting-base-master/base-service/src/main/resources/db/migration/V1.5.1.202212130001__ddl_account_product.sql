ALTER TABLE `service_accounting_base`.`account_product`
    ADD COLUMN `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '0-normal, 1-delete' AFTER `remark`;