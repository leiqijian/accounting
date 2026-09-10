ALTER TABLE `service_accounting_statement`.`accounting_schedule`
    ADD COLUMN `settlement_amount` DECIMAL(26, 0) NOT NULL DEFAULT '0' COMMENT 'settlement amount, unit:cent' AFTER `accounting_date`,
    MODIFY COLUMN `accounting_amount` decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'accounting_amount, unit:cent' AFTER `settlement_amount`,
    ADD COLUMN `fee_amount` DECIMAL(26,0)  NOT NULL DEFAULT '0' COMMENT 'fee amount, unit:cent' AFTER `accounting_amount`,
    ADD COLUMN `tax_amount` DECIMAL(26,0)  NOT NULL DEFAULT '0' COMMENT 'tax amount, unit:cent' AFTER `fee_amount`,
    ADD COLUMN `currency` varchar(3) DEFAULT NULL COMMENT 'currency' AFTER `tax_amount`,
    ADD COLUMN `product_code` varchar(32) DEFAULT '' COMMENT 'ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY' AFTER `unique_id`,
    ADD COLUMN `card_type` varchar(32) DEFAULT '' COMMENT 'CREDIT_CARD or DEBIT_CARD' AFTER `product_code`,
    ADD COLUMN `card_brand` varchar(64) DEFAULT '' COMMENT 'VISA,MASTERCARD ELO..' AFTER `card_type`;
