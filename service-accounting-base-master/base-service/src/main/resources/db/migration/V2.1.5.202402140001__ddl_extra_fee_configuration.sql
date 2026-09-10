CREATE TABLE service_accounting_base.extra_income_configuration
(
    id                    bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK ID',
    active_month          int unsigned NOT NULL COMMENT 'Active month format:yyyyMM',
    account_id            bigint unsigned NOT NULL DEFAULT '0' COMMENT 'Account Id',
    country_code          varchar(2)     NOT NULL DEFAULT '' COMMENT 'CountryEnum',
    transaction_type_code varchar(32)    NOT NULL DEFAULT '' COMMENT 'TransactionTypeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS',
    product_code          varchar(32)    NOT NULL DEFAULT '' COMMENT 'Product Code',
    installment_begin     int(4) unsigned NOT NULL DEFAULT '0' COMMENT 'Installment begin stage 0~360',
    installment_end       int(4) unsigned NOT NULL DEFAULT '360' COMMENT 'Installment end stage 0~360',
    fee_name              varchar(40)    NOT NULL COMMENT 'Fee name, TRANSACTION_FEE, TAX_IOF, TAX_IVA, FX',
    fee_type              varchar(32)    NOT NULL COMMENT 'Fee type: TRANSACTION_FEE, TAX, FX',
    fee_group             varchar(32)    NOT NULL DEFAULT 'EXTRA_FEE' COMMENT 'ExtraFeeGroup: TRANSACTION_FEE/EXTRA_FEE/TAX/EXTRA_TAX/FX/EXTRA_FX/FX_LOSE',
    fee_on                varchar(32)    NOT NULL COMMENT 'Fee On: ON_AMOUNT, ON_FEE',
    fee_model             tinyint(4) NOT NULL DEFAULT '0' COMMENT 'Fee model 0: FIXED; 1: PERCENTAGE',
    volume                decimal(26, 6) NOT NULL DEFAULT '0.000000' COMMENT 'Fee volume',
    currency              varchar(3)     NOT NULL COMMENT 'Fee currency',
    created_time          datetime                DEFAULT NULL COMMENT 'Created time',
    updated_time          datetime                DEFAULT NULL COMMENT 'Updated time',
    created_by            bigint unsigned DEFAULT '0' COMMENT 'Created by',
    updated_by            bigint unsigned DEFAULT '0' COMMENT 'Updated by',
    version               int            NOT NULL DEFAULT '1' COMMENT 'Optimistic locking flag',
    del_flag              tinyint(1) NOT NULL DEFAULT '0' COMMENT 'DelFlag: 0-normal, 1-delete',
    remark                varchar(200)            DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    KEY                   idx_active_config (account_id, active_month) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='extra_fee_configuration';


DELETE FROM service_accounting_base.cost_configuration WHERE apply_on=1;
DELETE FROM service_accounting_base.cost_configuration_card WHERE apply_on=1;

ALTER TABLE service_accounting_base.cost_configuration DROP COLUMN apply_on;
ALTER TABLE service_accounting_base.cost_configuration_card DROP COLUMN apply_on;
