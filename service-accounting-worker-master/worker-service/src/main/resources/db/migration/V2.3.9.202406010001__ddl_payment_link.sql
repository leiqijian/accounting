ALTER TABLE `service_accounting_worker`.`payment_link`
    ADD COLUMN `payment_document_url` VARCHAR (255) DEFAULT NULL
    COMMENT 'download url which upload from dashboard'
    AFTER `user_phone`;

ALTER TABLE `service_accounting_worker`.`payment_link`
    ADD COLUMN `supplement_document_url` VARCHAR (255) DEFAULT NULL
    COMMENT 'download url which upload from payment link service (cashier)'
    AFTER `user_phone`;
