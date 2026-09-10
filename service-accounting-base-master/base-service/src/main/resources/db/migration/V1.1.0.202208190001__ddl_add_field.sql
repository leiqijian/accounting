ALTER TABLE `service_accounting_base`.`payment_config`
    ADD UNIQUE INDEX `udx_account_product`(`account_id`, `product_code`) USING BTREE;

ALTER TABLE `service_accounting_base`.`payment_config`
    ADD COLUMN `priority` int UNSIGNED NULL DEFAULT 999 COMMENT 'The priority of different payment channels for the same account, the smaller the value, the higher the priority' AFTER `product_code`,
    ADD COLUMN `operation_method` tinyint UNSIGNED NULL DEFAULT 1 COMMENT 'operation_method, 0:auto payment; 1:manual payment' AFTER `json_params`;

-- DIDI daily auto funds collection
UPDATE `service_accounting_base`.`payment_config` SET operation_method = 0 WHERE account_id = 83864700725755914;