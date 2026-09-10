CREATE TABLE `payment_config`
(
    `id`                    bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    `account_id`            bigint          NOT NULL COMMENT 'ref:account.id',
    `product_code`          varchar(32)     NOT NULL COMMENT 'product code',
    `max_amount`            decimal(26, 0)  NOT NULL COMMENT 'single maximum transaction amount, unit:cent',
    `api_key`               varchar(128)    NOT NULL COMMENT 'x-api-key',
    `json_params`           varchar(800) DEFAULT NULL COMMENT 'Json Dynamic expansion parameters',
    `created_time`          datetime     DEFAULT NULL COMMENT 'created_time',
    `updated_time`          datetime     DEFAULT NULL COMMENT 'last update time',
    `version`               int unsigned DEFAULT '1' COMMENT 'optimistic locking',
    `status`                tinyint(4)   DEFAULT '0' COMMENT '0-disable, 1-enable',
    `del_flag`              tinyint(1)   DEFAULT '0' COMMENT '0-normal, 1-delete',
    `remark`                varchar(200) DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='payment_config';