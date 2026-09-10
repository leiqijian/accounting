ALTER TABLE `service_accounting_statement`.`global_account`
    ADD COLUMN `state` tinyint(4) UNSIGNED NOT NULL DEFAULT 1 COMMENT 'state: 0-DISABLE，1-ENABLE' AFTER `currency`;

ALTER TABLE `service_accounting_statement`.`global_statement`
    CHANGE COLUMN `global_id` `global_account_id` bigint UNSIGNED NOT NULL COMMENT 'group account id, ref: global_account.id' AFTER `id`,
    CHANGE COLUMN `account_id` `sub_account_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT 'topup or transfer_out to target subAccountId, when trade_type=SELF then account_id=0' AFTER `global_account_id`;


