CREATE TABLE service_accounting_statement.transaction_cost
(
    id                    bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK ID',
    transaction_id        bigint          NOT NULL COMMENT 'FK transaction id',
    unique_id             varchar(64)     NOT NULL COMMENT 'From global transaction system uniqueId, reference task_fee_calculation.unique_id',
    merchant_id           bigint          NOT NULL COMMENT 'Merchant id',
    account_id            bigint          NOT NULL COMMENT 'AccountId',
    transaction_type_code varchar(32)     NOT NULL COMMENT 'TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS',
    product_code          varchar(32)     NOT NULL COMMENT 'Product code',
    vendor                varchar(32)     NOT NULL COMMENT 'Vendor Code',
    amount                decimal(26)     NOT NULL DEFAULT '0' COMMENT 'Transaction amount, Unit: cent',
    currency              varchar(3)      NOT NULL COMMENT 'Transaction currency',
    amount_usd            decimal(26)     NOT NULL DEFAULT '0' COMMENT 'Amount converted into USD, Unit: cent',
    fee_usd               decimal(26)     NOT NULL DEFAULT '0' COMMENT 'Transaction fee Amount converted into USD, Unit: cent',
    cost_fee              decimal(26, 6)  NOT NULL DEFAULT '0.000000' COMMENT 'Cost Fee(USD), Unit: cent',
    cost_tax              decimal(26, 6)  NOT NULL DEFAULT '0.000000' COMMENT 'Cost Tax(USD), Unit: cent',
    cost_fx               decimal(26, 6)  NOT NULL DEFAULT '0.000000' COMMENT 'Cost FX(USD), Unit: cent',
    cost_other            decimal(26, 6)  NOT NULL DEFAULT '0.000000' COMMENT 'Other cost(USD) besides costFee, costTax, costFx. Unit: cent',
    transaction_time      datetime                 DEFAULT NULL COMMENT ' The original task_fee_calculation.transaction_time UTC+0',
    created_time          datetime                 DEFAULT NULL COMMENT 'Created time',
    updated_time          datetime                 DEFAULT NULL COMMENT 'Updated time',
    version               int             NOT NULL DEFAULT '1' COMMENT 'Optimistic locking flag',
    del_flag              tinyint(1)      NOT NULL DEFAULT '0' COMMENT 'DelFlag 0:normal; 1:delete',
    remark                varchar(200)             DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id),
    UNIQUE KEY udx_transaction_id (transaction_id) USING BTREE,
    UNIQUE KEY udx_unique_id (unique_id) USING BTREE,
    KEY idx_merchant_account (merchant_id, account_id) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='Transaction cost record';
