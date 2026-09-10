ALTER TABLE `service_accounting_worker`.`card_tokenization`
    ADD COLUMN `token_md5` varchar(128) NULL DEFAULT NULL COMMENT 'token_md5' AFTER `token_id`;

ALTER TABLE `service_accounting_worker`.`card_tokenization`
    ADD UNIQUE KEY `idx_token_md5` (`merchant_code`,`token_md5`) USING BTREE;

ALTER TABLE `service_accounting_worker`.`card_tokenization` DROP INDEX idx_token_id;

ALTER TABLE `service_accounting_worker`.`card_tokenization`
    MODIFY COLUMN card_holder_name varchar (300);

ALTER TABLE `service_accounting_worker`.`card_tokenization`
    MODIFY COLUMN `country_code` varchar (2) DEFAULT NULL;