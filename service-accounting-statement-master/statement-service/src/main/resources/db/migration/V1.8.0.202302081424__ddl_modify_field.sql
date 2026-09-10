ALTER TABLE `service_accounting_statement`.`biz_topup`
    ADD COLUMN `biz_type_code` varchar(32) NOT NULL COMMENT 'topup secondary business' AFTER `account_id`;

ALTER TABLE `service_accounting_statement`.`biz_transfer_out`
    ADD COLUMN `biz_type_code` varchar(32) NOT NULL COMMENT 'transfer_out secondary business' AFTER `account_id`;
