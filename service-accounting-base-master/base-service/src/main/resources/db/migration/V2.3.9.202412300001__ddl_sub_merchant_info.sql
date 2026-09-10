CREATE TABLE `sub_merchant_info`
(
    `id`                    bigint       NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `merchant_id`           bigint       NOT NULL COMMENT 'merchant id',
    `sub_merchant_id`       varchar(128) NOT NULL COMMENT 'sub_merchant_id',
    `merchant_code`         varchar(128) NOT NULL COMMENT 'merchant info: code',
    `merchant_name`         varchar(128) NOT NULL COMMENT 'Merchant Name',
    `owner`                 varchar(32)  DEFAULT NULL DEFAULT 'TBD' COMMENT 'APAC(CN), SSL(MX), BR(BR), US(US),IT(inner merchant),TBD(wait confirm)',
    `legal_name`            varchar(128) DEFAULT NULL COMMENT 'Legal Name',
    `commercial_name`       varchar(128) DEFAULT NULL COMMENT 'Commercial Name',
    `tax_id`                varchar(128) DEFAULT NULL COMMENT 'Tax ID',
    `incorporation_country` varchar(128) DEFAULT NULL COMMENT 'incorporation country',
    `website`               json         DEFAULT NULL COMMENT 'Website/App Download Link',
    `service_applying`      json         DEFAULT NULL COMMENT 'service applying',
    `industry`              varchar(64)  DEFAULT NULL COMMENT 'Industry',
    `cards_appicable`       json         DEFAULT NULL COMMENT 'Card appicable',
    `merchant_remark`       varchar(400) DEFAULT NULL COMMENT 'merchant remark',
    `created_time`          datetime     DEFAULT NULL,
    `updated_time`          datetime     DEFAULT NULL,
    `del_flag`              tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_merchant_id_sub_merchent_id` (`merchant_id`,`sub_merchant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT = 'sub_merchant_info';