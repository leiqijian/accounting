CREATE TABLE `cost_configuration`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK ID',
    `account_id`   bigint unsigned NOT NULL COMMENT 'Account Id',
    `vendor_code`  varchar(32)     NOT NULL COMMENT 'Vendor Code',
    `product_code` varchar(32)     NOT NULL COMMENT 'Product Code',
    `active_month` int unsigned    NOT NULL COMMENT 'Active month format:yyyyMM',
    `apply_on`     tinyint         NOT NULL COMMENT 'Apply on: 1:COST; 2: REVENUE',
    `fee_model`        tinyint         NOT NULL COMMENT 'Fee model 0: FIXED; 1: PERCENTAGE',
    `tax_rule`     tinyint         NOT NULL COMMENT 'Tax rule: 1: TAX-BEFORE; 2:TAX-AFTER',
    `fee_name`     varchar(40)     NOT NULL COMMENT 'Fee name, TRANSACTION_FEE, TAX_IOF, TAX_IVA, FX',
    `fee_type`     varchar(32)     NOT NULL COMMENT 'Fee type: TRANSACTION_FEE, TAX, FX',
    `fee_on`       varchar(32)     NOT NULL COMMENT 'Fee On: ON_AMOUNT, ON_FEE',
    `volume`       decimal(26, 6)  NOT NULL DEFAULT '0.000000' COMMENT 'Fee value',
    `currency`     varchar(3)      NOT NULL COMMENT 'Fee currency',
    `created_time` datetime                 DEFAULT NULL COMMENT 'Created time',
    `updated_time` datetime                 DEFAULT NULL COMMENT 'Updated time',
    `created_by`   bigint unsigned          DEFAULT '0' COMMENT 'Created by',
    `updated_by`   bigint unsigned          DEFAULT '0' COMMENT 'Updated by',
    `version`      int             NOT NULL DEFAULT '1' COMMENT 'Optimistic locking flag',
    `del_flag`     tinyint(1)      NOT NULL DEFAULT '0' COMMENT 'DelFlag: 0-normal, 1-delete',
    `remark`       varchar(200)             DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_account_id` (`account_id`) USING BTREE,
    KEY `idx_vendor_code` (`vendor_code`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='Cost configuration';