CREATE TABLE service_accounting_base.cost_configuration_card
(
    id                bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK ID',
    active_month      int(6) unsigned NOT NULL COMMENT 'Active month format:yyyyMM',
    account_id        bigint unsigned NOT NULL COMMENT 'Account Id',
    country_code      varchar(2)     NOT NULL DEFAULT '' COMMENT 'CountryCodeEnum',
    vendor_code       varchar(32)    NOT NULL DEFAULT '' COMMENT 'VendorCode',
    direction_type    varchar(32)    NOT NULL DEFAULT '' COMMENT 'DirectionTypeEnum: SETTLED, REFUND, CHARGE_BACK',
    card_type         varchar(16)    NOT NULL DEFAULT '' COMMENT 'CardTypeEnum: CREDIT_CARD, DEBIT_CARD',
    card_group        varchar(16)    NOT NULL DEFAULT '' COMMENT 'CardGroupEnum: VISA, MASTERCARD, ELO, AMEX, DEFAULT...',
    installment_begin int(4) NOT NULL DEFAULT '0' COMMENT 'Installment begin stage',
    installment_end   int(4) NOT NULL DEFAULT '360' COMMENT 'Installment end stage',
    fee_name          varchar(40)    NOT NULL DEFAULT '' COMMENT 'FeeName, TRANSACTION_FEE, TAX_IOF, TAX_IVA, FX',
    fee_type          varchar(32)    NOT NULL COMMENT 'FeeType: TRANSACTION_FEE, 3DS_FEE, ANTI_FRAUD_FEE, TAX, FX',
    fee_group         varchar(32)    NOT NULL COMMENT 'FeeGroup: TRANSACTION_FEE, TAX, FX',
    fee_on            varchar(16)    NOT NULL COMMENT 'FeeOn: ON_AMOUNT, ON_FEE',
    fee_model         tinyint        NOT NULL COMMENT 'FeeModel: 0: FIXED; 1: PERCENTAGE',
    volume            decimal(26, 6) NOT NULL DEFAULT '0.000000' COMMENT 'FeeValue',
    min_volume        decimal(26, 0) NOT NULL DEFAULT '0.000000' COMMENT 'FeeMinValue',
    max_volume        decimal(26, 0) NOT NULL DEFAULT '0.000000' COMMENT 'FeeMaxValue',
    currency          varchar(3)     NOT NULL COMMENT 'FeeCurrency USD, BRL, MXN, COP, CLP...',
    apply_on          tinyint        NOT NULL DEFAULT '0' COMMENT 'ApplyOn: 0: COST;  1: REVENUE',
    created_time      datetime                DEFAULT NULL COMMENT 'CreatedTime',
    updated_time      datetime                DEFAULT NULL COMMENT 'UpdatedTime',
    created_by        bigint unsigned DEFAULT '0' COMMENT 'Created by',
    updated_by        bigint unsigned DEFAULT '0' COMMENT 'Updated by',
    version           int            NOT NULL DEFAULT '1' COMMENT 'Optimistic locking flag',
    del_flag          tinyint(1) NOT NULL DEFAULT '0' COMMENT 'DelFlag: 0-normal, 1-delete',
    remark            varchar(200)            DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    KEY               idx_account_id (account_id) USING BTREE,
    KEY               idx_vendor_code (country_code, vendor_code) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=439813213102165500 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='Card cost configuration';

ALTER TABLE service_accounting_base.cost_configuration
    ADD COLUMN fee_group varchar(32) NOT NULL DEFAULT 'TRANSACTION_FEE' COMMENT 'FeeGroup: TRANSACTION_FEE, TAX, FX' AFTER fee_type;

UPDATE service_accounting_base.cost_configuration SET fee_group = fee_type;
