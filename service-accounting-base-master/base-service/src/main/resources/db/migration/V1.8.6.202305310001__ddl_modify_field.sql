ALTER TABLE `service_accounting_base`.`payment_config`
    MODIFY COLUMN `json_params` json NULL COMMENT 'Json dynamic expansion parameters' AFTER `api_key`;