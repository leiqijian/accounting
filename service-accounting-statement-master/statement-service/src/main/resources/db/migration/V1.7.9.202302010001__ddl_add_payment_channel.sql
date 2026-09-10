-- biz_refund 表添加 payment_channel 字段
ALTER TABLE `service_accounting_statement`.`biz_refund`
    ADD COLUMN `payment_channel` varchar(32) NULL COMMENT 'payment channel PIX, SPEI...' AFTER `transaction_type_code`;

-- -- biz_topup 表添加 payment_channel 字段
ALTER TABLE `service_accounting_statement`.`biz_topup`
    ADD COLUMN `payment_channel` varchar(32) NULL COMMENT 'payment channel PIX, SPEI...' AFTER `transaction_type_code`;