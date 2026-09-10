ALTER TABLE `service_accounting_statement`.`biz_transfer_out`
    ADD COLUMN `product_code` varchar(32) NULL COMMENT 'payment method productCode PIX, SPEI...' AFTER `transaction_type_code`,
    ADD COLUMN `payment_action` tinyint(4) NULL COMMENT 'payment action 0: AUTO, 1:MANUAL'  AFTER `product_code`,
    ADD COLUMN `third_party_trans_id` varchar(128) NULL COMMENT 'third party service return transactionId' AFTER `remark`;

ALTER TABLE `service_accounting_statement`.`biz_transfer_out`
    MODIFY COLUMN `remark` varchar(1000)  NULL DEFAULT '' COMMENT 'remark' AFTER `del_flag`;