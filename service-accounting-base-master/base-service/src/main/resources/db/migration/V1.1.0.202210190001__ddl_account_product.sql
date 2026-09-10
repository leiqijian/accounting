ALTER TABLE `service_accounting_base`.`account_product`
    ADD COLUMN `open_model` tinyint NOT NULL DEFAULT 0
    COMMENT 'ProductOpenModelEnum:0-BUSINESS/1-TRANSFER_OUT/2-TOPUP' AFTER `product_code`;