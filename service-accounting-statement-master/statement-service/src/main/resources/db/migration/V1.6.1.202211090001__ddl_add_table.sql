CREATE TABLE `service_accounting_statement`.`hourly_exchange_rate`
(
    `id`              bigint unsigned  NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    `source_currency` varchar(3)       NOT NULL COMMENT 'exchange from currency',
    `target_currency` varchar(3)       NOT NULL COMMENT 'exchange to currency',
    `exchange_time`   datetime         NOT NULL COMMENT 'exchange datetime(UTC+0), format: yyyy-MM-dd HH:00:00',
    `exchange_rate`   decimal(26, 6)   NOT NULL COMMENT 'day of exchange to rate',
    `created_time`    datetime         NOT NULL COMMENT 'created time',
    `updated_time`    datetime         NOT NULL COMMENT 'updated time',
    `version`         int unsigned     NOT NULL DEFAULT '1' COMMENT 'optimistic locking',
    `del_flag`        tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0-normal,1-delete',
    `remark`          varchar(200)              DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `udx_hourly_rate` (`source_currency`, `target_currency`, `exchange_time`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='hourly exchange rate';