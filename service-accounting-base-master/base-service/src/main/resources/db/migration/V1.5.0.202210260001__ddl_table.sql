CREATE TABLE service_accounting_base.biz_fee_configuration
(
    id               bigint unsigned         NOT NULL AUTO_INCREMENT COMMENT 'Primary key Id',
    business_type    varchar(20)             NOT NULL COMMENT 'Business type enum, TOPUP, TRANSFER_OUT, REFUND',
    operation_method tinyint unsigned        NOT NULL DEFAULT '1' COMMENT 'operation method, 0:auto payment; 1:manual payment',
    account_id       bigint unsigned         NOT NULL COMMENT 'AccountId',
    fee_name         varchar(32)             NOT NULL DEFAULT '' COMMENT 'Fee name',
    fee_type         varchar(32)             NOT NULL COMMENT 'FeeTypeCodeEnum: TRANSACTION_FEE, TAX',
    fee_model        tinyint(4)              NOT NULL COMMENT 'FeeValueModelEnum: fixed:0; percent:1',
    fee_on           varchar(32)             NOT NULL COMMENT 'FeeOnEnum: AMOUNT/SETTLE_AMOUNT',
    fee_value        decimal(26, 6) unsigned NOT NULL DEFAULT 0 COMMENT 'Fixed amount value or percentage amount ratio',
    min_amount       decimal(26, 0) unsigned NOT NULL DEFAULT 0 COMMENT 'Single transaction min fee amount unit:cent',
    max_amount       decimal(26, 0) unsigned NOT NULL DEFAULT 0 COMMENT 'Single transaction max fee amoun unit:cent',
    version          int(11) unsigned        NOT NULL DEFAULT '1' COMMENT 'Optimistic locking, The higher the value, the higher the priority',
    created_time     datetime                         DEFAULT NULL,
    updated_time     datetime                         DEFAULT NULL,
    del_flag         tinyint(1)              NOT NULL DEFAULT '0' COMMENT 'del flag 0:normal, 1:delete',
    remark           varchar(200)                     DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id),
    KEY idx_account_id (account_id) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='biz transaction fee configuration';


ALTER TABLE service_accounting_base.payment_config
    CHANGE COLUMN product_code payment_channel varchar(32) NOT NULL COMMENT 'payment channel code' AFTER account_id;


-- init config
INSERT INTO service_accounting_base.biz_fee_configuration
(id, business_type, operation_method, account_id, fee_name, fee_type, fee_model, fee_on,
 fee_value, min_amount, max_amount, version, created_time, updated_time, del_flag, remark)
SELECT id,
       'TRANSFER_OUT',
       '0',
       account_id,
       fee_type_code,
       fee_type_code,
       fee_value_model,
       fee_on,
       fee_value,
       min_fee_amount,
       max_fee_amount,
       version,
       created_time,
       updated_time,
       del_flag,
       remark
FROM service_accounting_base.monthly_fee_configuration
WHERE direction_type = 'AUTO_TRANSFER_OUT'
  AND active_month = 202210
  AND fee_type_code <> 'FX_LOSE';