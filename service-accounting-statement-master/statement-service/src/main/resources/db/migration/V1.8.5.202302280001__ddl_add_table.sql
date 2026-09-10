CREATE TABLE service_accounting_statement.transaction_biz
(
    id                   bigint unsigned  NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    request_id           varchar(64)      NOT NULL DEFAULT '' COMMENT 'unique request id',
    transaction_id       bigint unsigned  NOT NULL COMMENT 'transaction id',
    bill_id              bigint unsigned  NOT NULL DEFAULT '0' COMMENT 'reference account_daily_bill.id',
    merchant_id          bigint unsigned  NOT NULL COMMENT 'reference merchant.id',
    account_id           bigint unsigned  NOT NULL COMMENT 'reference account.id',
    reference_id         varchar(64)               DEFAULT '' COMMENT 'scene for refund',
    payment_config_id    bigint unsigned           DEFAULT '0' COMMENT 'FK ref payment_config.id',
    transaction_type     varchar(32)      NOT NULL COMMENT 'TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS',
    business_type        varchar(32)      NOT NULL COMMENT 'business type: TOPUP, REFUND, TRANSFER_OUT, EXCHANGE, ADJUSTMENT',
    payment_channel      varchar(32)      NOT NULL COMMENT 'payment channel PIX, SPEI...',
    operate_mode         tinyint unsigned NOT NULL DEFAULT '0' COMMENT 'operate mode 0: AUTO, 1:MANUAL',
    transaction_time     datetime         NOT NULL COMMENT 'transaction time (UTC 0)',
    transaction_amount   decimal(26, 0)   NOT NULL DEFAULT '0' COMMENT 'transaction amount, unit:cent',
    amount_pon           decimal(2, 0)    NOT NULL COMMENT 'positive or negative; positive :+1, negative:-1',
    transaction_currency varchar(3)       NOT NULL COMMENT 'transaction currency',
    exchange_rate        decimal(26, 6)   NOT NULL DEFAULT '1.000000' COMMENT 'exchange rate',
    settlement_amount    decimal(26, 0)   NOT NULL DEFAULT '0' COMMENT 'Amount after exchange rate transfer (before deduction of fees and taxes),unit:cent',
    settlement_currency  varchar(3)       NOT NULL COMMENT 'settlement currency',
    fee_amount           decimal(26, 0)            DEFAULT '0' COMMENT 'fee amount ,unit:cent',
    tax_amount           decimal(26, 0)            DEFAULT '0' COMMENT 'tax amount ,unit:cent',
    settlement_status    varchar(32)      NOT NULL DEFAULT 'WAITING' COMMENT 'StatusEnum: WAITING/PROCESSING/SUCCESS/FAILED',
    settlement_time      datetime                  DEFAULT NULL COMMENT 'after completed settlement time (UTC 0)',
    comments             varchar(400)              DEFAULT '' COMMENT 'transaction comments',
    created_time         datetime         NOT NULL COMMENT 'created time',
    updated_time         datetime         NOT NULL COMMENT 'updated time',
    created_by           bigint                    DEFAULT '0' COMMENT 'created by',
    updated_by           bigint                    DEFAULT '0' COMMENT 'updated by',
    del_flag             tinyint(1)       NOT NULL DEFAULT '0' COMMENT '0-normal，1-delete',
    extend_info          json                      DEFAULT NULL COMMENT 'extend json info',
    version              int              NOT NULL DEFAULT '1' COMMENT 'optimistic locking',
    remark               varchar(1000)             DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY udx_request_id (request_id) USING BTREE,
    UNIQUE KEY udx_transaction_id (transaction_id) USING BTREE,
    KEY idx_account_biz (account_id, transaction_id) USING BTREE,
    KEY idx_account_bill (account_id, bill_id) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT ='transaction biz(account topup, chargeback, refund, transfer_out)';


-- data migration
INSERT INTO service_accounting_statement.transaction_biz (id,request_id,transaction_id,bill_id,merchant_id,account_id,reference_id,payment_config_id,transaction_type,business_type,payment_channel,operate_mode,transaction_time,transaction_amount,amount_pon,transaction_currency,exchange_rate,settlement_amount,settlement_currency,fee_amount,tax_amount,settlement_status,settlement_time,comments,created_time,updated_time,created_by,updated_by,del_flag,extend_info,version,remark)
SELECT id,id AS request_id,transaction_id,bill_id,merchant_id,account_id,'' AS reference_id,'0' AS payment_config_id,transaction_type_code,biz_type_code,payment_channel,operate_mode,transaction_time,amount,1 AS amount_pon,currency,fx_rate,settlement_amount,settlement_currency,fee,tax,settle_status,settle_time,comments,created_time,updated_time,created_by,updated_by,del_flag,NULL AS extend_info,version,remark
FROM service_accounting_statement.biz_topup;

INSERT INTO service_accounting_statement.transaction_biz (id,request_id,transaction_id,bill_id,merchant_id,account_id,reference_id,payment_config_id,transaction_type,business_type,payment_channel,operate_mode,transaction_time,transaction_amount,amount_pon,transaction_currency,exchange_rate,settlement_amount,settlement_currency,fee_amount,tax_amount,settlement_status,settlement_time,comments,created_time,updated_time,created_by,updated_by,del_flag,extend_info,version,remark)
SELECT id,id AS request_id,transaction_id,bill_id,merchant_id,account_id,'' AS reference_id,payment_config_id,transaction_type_code,biz_type_code,payment_channel,operate_mode,transaction_time,amount,'-1' AS amount_pon,currency,fx_rate,settlement_amount,settlement_currency,fee,tax,settle_status,settle_time,comments,created_time,updated_time,created_by,updated_by,del_flag,NULL AS extend_info,version,remark
FROM service_accounting_statement.biz_transfer_out;

