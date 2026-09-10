ALTER TABLE `service_accounting_statement`.`transaction_fee`
    ADD COLUMN `direction_type` varchar(32) NULL DEFAULT 'SETTLED' COMMENT 'DirectionTypeEnum: SETTLED/REFUND/CHARGE_BACK/REJECTED' AFTER `product_code`;
