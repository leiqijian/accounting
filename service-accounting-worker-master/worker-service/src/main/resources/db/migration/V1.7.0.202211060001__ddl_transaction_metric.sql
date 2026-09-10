CREATE TABLE `service_accounting_worker`.`transaction_metric`
(
    `id`                bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    `merchant_code`     varchar(64)    NOT NULL,
    `country`           varchar(2)     NOT NULL,
    `transaction_type`  varchar(100)   NOT NULL,
    `count_transaction` int            NOT NULL DEFAULT 0,
    `sum_amount`        decimal(26, 0) NOT NULL DEFAULT 0,
    `date`              timestamp      NOT NULL,
    `currency`          varchar(3)     NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='transaction metric';

ALTER TABLE `service_accounting_worker`.`transaction_metric`
    ADD INDEX `idx_merchant_code_country_date`(`merchant_code`, `country`, `date`) USING BTREE;