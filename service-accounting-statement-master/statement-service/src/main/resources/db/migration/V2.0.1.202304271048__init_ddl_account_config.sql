CREATE TABLE `service_accounting_statement`.`account_config`
(
    `id`                   bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    `merchant_id`          bigint(0) NOT NULL,
    `account_id`           bigint(0) NOT NULL,
    `balance_alarm_config` text NOT NULL,
    `created_time`         datetime(0) NULL DEFAULT NULL,
    `updated_time`         datetime(0) NULL DEFAULT NULL,
    `created_by`           bigint(0) NULL DEFAULT 0,
    `updated_by`           bigint(0) NULL DEFAULT 0,
    `version`              bigint(0) UNSIGNED NULL DEFAULT 1 COMMENT 'optimistic locking',
    `del_flag`             tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete',
    PRIMARY KEY (`id`)
);