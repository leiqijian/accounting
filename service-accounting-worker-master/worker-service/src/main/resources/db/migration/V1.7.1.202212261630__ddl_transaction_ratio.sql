CREATE TABLE `service_accounting_worker`.`transaction_ratio`
(
    `id`               bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    `merchant_code`    varchar(64)   NOT NULL,
    `country_code`     varchar(2)    NOT NULL,
    `transaction_type` varchar(32)   NOT NULL,
    `product_code`     varchar(32)   NOT NULL,
    `total_count`      int           NOT NULL DEFAULT 0,
    `success_count`    int           NOT NULL DEFAULT 0,
    `success_rate`     decimal(6, 4) NOT NULL DEFAULT 0,
    `date`             timestamp     NOT NULL,
    `created_time`     datetime      DEFAULT NULL,
    `del_flag`         tinyint       NOT NULL DEFAULT '0' COMMENT '0-normal，1-delete',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='transaction ratio';

ALTER TABLE `service_accounting_worker`.`transaction_ratio`
    ADD INDEX `idx_merchant_code_country_code_transaction_type_date`(`merchant_code`, `country_code`,`transaction_type`, `date`) USING BTREE;