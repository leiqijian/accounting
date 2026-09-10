ALTER TABLE `service_accounting_worker`.`payment_link`
    ADD COLUMN `others` json NULL COMMENT 'json extend data' AFTER `user_phone`;

ALTER TABLE `service_accounting_worker`.`shopify`
    ADD COLUMN `others` json NULL COMMENT 'json extend data' AFTER `user_phone`;

ALTER TABLE `service_accounting_worker`.`shoplazza`
    ADD COLUMN `others` json NULL COMMENT 'json extend data' AFTER `user_phone`;