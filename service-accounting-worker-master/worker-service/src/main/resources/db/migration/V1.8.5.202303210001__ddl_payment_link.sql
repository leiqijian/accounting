ALTER TABLE `service_accounting_worker`.`payment_link`
DROP INDEX `idx_merchant_reference`,
ADD INDEX `idx_merchant_reference`(`merchant_reference` ASC) USING BTREE;