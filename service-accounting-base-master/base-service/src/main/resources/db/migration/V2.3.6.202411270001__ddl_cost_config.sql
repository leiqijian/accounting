CREATE TABLE service_accounting_base.cost_configuration_version
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK ID',
    `cost_type`    varchar(32) NOT NULL DEFAULT 'UNIVERSAL' COMMENT 'UNIVERSAL, CARD',
    `created_time` datetime             DEFAULT NULL COMMENT 'Created time',
    `updated_time` datetime             DEFAULT NULL COMMENT 'Updated time',
    `version`      int         NOT NULL DEFAULT '1' COMMENT 'Optimistic locking flag',
    `del_flag`     tinyint(1) NOT NULL DEFAULT '0' COMMENT 'DelFlag: 0-normal, 1-delete',
    `remark`       varchar(200)         DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `udx_config_type` (`cost_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cost configuration version';

INSERT INTO service_accounting_base.cost_configuration_version
set cost_type='GENERAL', created_time=now(), updated_time=now(), version=20250101, del_flag=0,remark='old logic';

INSERT INTO service_accounting_base.cost_configuration_version
set cost_type='UNIVERSAL', created_time=now(), updated_time=now(), version=20250101, del_flag=0,remark='new logic';

INSERT INTO service_accounting_base.cost_configuration_version
set cost_type='CARD', created_time=now(), updated_time=now(), version=20250101, del_flag=0,remark='';

INSERT INTO service_accounting_base.cost_configuration_version
set cost_type='EXTRA_INCOME', created_time=now(), updated_time=now(), version=20250101, del_flag=0,remark='';

CREATE TABLE service_accounting_base.cost_configuration_universal
(
    `id`                    bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK ID',
    `active_version`        int            NOT NULL DEFAULT '20250101' COMMENT 'Active version ref:cost_configuration_version.version',
    `account_id`            bigint unsigned NOT NULL COMMENT 'Account Id',
    `country_code`          varchar(2)     NOT NULL DEFAULT '' COMMENT 'CountryCodeEnum',
    `transaction_type_code` varchar(32)    NOT NULL DEFAULT '' COMMENT 'TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS',
    `vendor_code`           varchar(32)    NOT NULL COMMENT 'Vendor Code',
    `product_code`          varchar(32)    NOT NULL COMMENT 'Product Code',
    `fee_model`             tinyint        NOT NULL COMMENT 'Fee model 0: FIXED; 1: PERCENTAGE',
    `fee_name`              varchar(40)    NOT NULL COMMENT 'Fee name, TRANSACTION_FEE, TAX_IOF, TAX_IVA, FX',
    `fee_type`              varchar(32)    NOT NULL COMMENT 'Fee type: TRANSACTION_FEE, TAX, FX',
    `fee_group`             varchar(32)    NOT NULL DEFAULT 'TRANSACTION_FEE' COMMENT 'FeeGroup: TRANSACTION_FEE, TAX, FX',
    `fee_on`                varchar(32)    NOT NULL COMMENT 'Fee On: ON_AMOUNT, ON_FEE',
    `volume`                decimal(26, 6) NOT NULL DEFAULT '0.000000' COMMENT 'Fee value',
    `currency`              varchar(3)     NOT NULL COMMENT 'Fee currency',
    `created_time`          datetime                DEFAULT NULL COMMENT 'Created time',
    `updated_time`          datetime                DEFAULT NULL COMMENT 'Updated time',
    `created_by`            bigint unsigned DEFAULT '0' COMMENT 'Created by',
    `updated_by`            bigint unsigned DEFAULT '0' COMMENT 'Updated by',
    `version`               int            NOT NULL DEFAULT '1' COMMENT 'Optimistic locking flag',
    `del_flag`              tinyint(1) NOT NULL DEFAULT '0' COMMENT 'DelFlag: 0-normal, 1-delete',
    `remark`                varchar(200)            DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                     `idx_vendor_code` (version, vendor_code) USING BTREE,
    KEY                     `idx_country_transaction_type` (version, country_code, transaction_type_code) USING BTREE,
    KEY                     `idx_account_config` (version, account_id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Universal Cost configuration';

ALTER TABLE service_accounting_base.cost_configuration
    CHANGE COLUMN active_month active_version int UNSIGNED NOT NULL DEFAULT '20250101' COMMENT 'Active version' AFTER id;

ALTER TABLE service_accounting_base.cost_configuration_card
    CHANGE COLUMN active_month active_version int UNSIGNED NOT NULL DEFAULT '20250101' COMMENT 'Active version' AFTER id;

ALTER TABLE service_accounting_base.extra_income_configuration
    CHANGE COLUMN active_month active_version int UNSIGNED NOT NULL DEFAULT '20250101' COMMENT 'Active version' AFTER id;


INSERT INTO service_accounting_base.cost_configuration (
       account_id, country_code, transaction_type_code, vendor_code, product_code, active_version, fee_model, tax_rule, fee_name, fee_type, fee_group, fee_on, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT account_id, country_code, transaction_type_code, vendor_code, product_code, 20250101,       fee_model, tax_rule, fee_name, fee_type, fee_group, fee_on, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark
FROM service_accounting_base.cost_configuration WHERE active_version=202501;

INSERT INTO service_accounting_base.cost_configuration_card(
 active_version, account_id, country_code, vendor_code, direction_type, card_type, card_group, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, min_volume, max_volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 20250101, account_id, country_code, vendor_code, direction_type, card_type, card_group, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, min_volume, max_volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark
FROM service_accounting_base.cost_configuration_card WHERE active_version = 202501;

INSERT INTO service_accounting_base.extra_income_configuration (
 active_version, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark)
SELECT 20250101, account_id, country_code, transaction_type_code, product_code, installment_begin, installment_end, fee_name, fee_type, fee_group, fee_on, fee_model, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark
FROM service_accounting_base.extra_income_configuration WHERE active_version=202501;

