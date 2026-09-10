-- service_accounting_base
-- CREATE DATABASE IF NOT EXISTS service_accounting_base DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
-- USE service_accounting_base;


CREATE TABLE account_fee_configuration (
 id bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
 account_id bigint NOT NULL COMMENT 'fk ref: account.id',
 account_product_id bigint NOT NULL COMMENT 'fk ref: account_product.id',
 product_code varchar(32) NOT NULL COMMENT 'ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY',
 fee_type_code varchar(32) NOT NULL COMMENT 'FeeTypeCodeEnum: TRANSACTION_FEE/FX/TAX/REFUND_FEE/CHARGE_BACK_FEE/WITHDRAW_FEE',
 monthly_volume_type tinyint NOT NULL COMMENT 'MonthlyVolumeTypeEnum: 0-amount/1-counts ',
 min_monthly_volume bigint NOT NULL COMMENT 'Min monthly volume',
 max_monthly_volume bigint NOT NULL COMMENT 'Max monthly volume',
 fee_value_model int NOT NULL COMMENT 'FeeValueModelEnum: fixed -0/percent -1',
 fee_value decimal(26, 6) NOT NULL COMMENT 'fixed value or percentage ratio',
 currency varchar(3) NOT NULL COMMENT 'currency',
 min_fee_amount bigint NOT NULL,
 max_fee_amount bigint NOT NULL,
 charge_on varchar(32) NOT NULL COMMENT 'ChargeOnEnum: TRANSACTION/DAY/MONTH',
 fee_on varchar(32) NOT NULL COMMENT 'FeeOnEnum: AMOUNT/SETTLE_AMOUNT',
 direction_type varchar(32) NOT NULL COMMENT 'DirectionTypeEnum: SETTLED/REFUND/CHARGE_BACK',
 instant_flag tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-non-instant,1-instant',
 created_time datetime NULL DEFAULT NULL,
 updated_time datetime NULL DEFAULT NULL,
 created_by bigint NULL DEFAULT 0,
 updated_by bigint(20) NULL DEFAULT 0,
 version int NULL DEFAULT 1 COMMENT 'optimistic locking',
 del_flag tinyint(1) NULL DEFAULT 0 COMMENT '0-normal,1-delete',
 remark varchar(200) NULL DEFAULT '' COMMENT 'remark',
 PRIMARY KEY (id) USING BTREE,
 INDEX udx_product_fee_type(account_product_id, fee_type_code) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'account_fee_configuration' ROW_FORMAT = DYNAMIC;


CREATE TABLE account_product (
 id bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
 account_id bigint NOT NULL,
 transaction_type_code varchar(32) NULL DEFAULT NULL COMMENT 'TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS',
 product_code varchar(32) NOT NULL COMMENT 'ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY',
 monthly_volume_type tinyint NULL DEFAULT NULL COMMENT 'MonthlyVolumeTypeEnum: 0-amount/1-counts ',
 timezone varchar(8) NULL DEFAULT NULL COMMENT 'local time zone',
 open_time timestamp NULL DEFAULT NULL COMMENT 'Account opening time',
 remark varchar(200) NULL DEFAULT '' COMMENT 'remark',
 PRIMARY KEY (id) USING BTREE,
 UNIQUE INDEX udx_account_product(account_id, product_code) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'account_product' ROW_FORMAT = DYNAMIC;


CREATE TABLE country_product (
 id bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
 country_code varchar(2) NULL DEFAULT NULL COMMENT 'CountryCodeEnum: BR/MX/US',
 `transaction_type_code` varchar(32)  NULL DEFAULT NULL COMMENT 'TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS',
 product_code varchar(32) NULL DEFAULT NULL COMMENT 'ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY',
 remark varchar(200) NULL DEFAULT '' COMMENT 'remark',
 PRIMARY KEY (id) USING BTREE,
 UNIQUE INDEX udx_country_product(country_code, transaction_type_code, product_code) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'country_product' ROW_FORMAT = DYNAMIC;


CREATE TABLE monthly_fee_configuration (
 id bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
 account_id bigint NULL DEFAULT NULL COMMENT 'fk',
 account_product_id bigint NOT NULL COMMENT 'fk',
 product_code varchar(32) NULL DEFAULT NULL COMMENT 'ProductCodeEnum',
 fee_type_code varchar(32) NOT NULL COMMENT 'FeeTypeCodeEnum: TRANSACTION_FEE/FX/TAX/REFUND_FEE/CHARGE_BACK_FEE/WITHDRAW_FEE',
 active_month int NOT NULL COMMENT 'yyyyMM',
 fee_value_model tinyint NOT NULL COMMENT 'FeeValueModelEnum: fixed -0/percent -1',
 fee_value decimal(26, 6) UNSIGNED NOT NULL COMMENT 'fixed value or percentage ratio',
 currency varchar(3) NOT NULL,
 min_fee_amount decimal(26, 0) NULL DEFAULT NULL,
 max_fee_amount decimal(26, 0) NULL DEFAULT NULL,
 charge_on varchar(32) NULL DEFAULT NULL COMMENT 'ChargeOnEnum: TRANSACTION/DAY/MONTH',
 fee_on varchar(32) NULL DEFAULT NULL COMMENT 'FeeOnEnum: AMOUNT/SETTLE_AMOUNT',
 direction_type varchar(32) NULL DEFAULT NULL COMMENT 'DirectionTypeEnum: SETTLED/REFUND/CHARGE_BACK',
 instant_flag tinyint(1) NULL DEFAULT 0 COMMENT 'instant flag: 0-non-instant,1-instant',
 created_time datetime NULL DEFAULT NULL,
 updated_time datetime NULL DEFAULT NULL,
 created_by bigint NULL DEFAULT 0,
 updated_by bigint NULL DEFAULT 0,
 version int NULL DEFAULT 1 COMMENT 'optimistic locking',
 del_flag tinyint(1) NULL DEFAULT 0 COMMENT '0-normal,1-delete',
 remark varchar(200) NULL DEFAULT '' COMMENT 'remark',
 PRIMARY KEY (id) USING BTREE,
 INDEX udx_month_fee_config(account_product_id, active_month, fee_type_code) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'monthly_account_fee_configuratio' ROW_FORMAT = DYNAMIC;
