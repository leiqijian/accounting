CREATE TABLE service_accounting_statement.transaction_payout
(
    id                 bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    unique_id          bigint          NOT NULL COMMENT 'unique_id',
    account_id         bigint          NOT NULL COMMENT 'account id',
    country_code       varchar(2)      NOT NULL COMMENT 'countryCode',
    product_code       varchar(32)    DEFAULT NULL COMMENT 'ProductCodeEnum: SPEI,PIX,TED...',
    amount             decimal(26, 0) DEFAULT NULL COMMENT 'payout amount unit: cent',
    currency           varchar(3)     DEFAULT NULL COMMENT 'currency: MXN, BRL, USD',
    transaction_status varchar(32)    DEFAULT NULL COMMENT 'StatusEnum: WAITING/PROCESSING/SUCCESS/FAILED',
    target_info        json           DEFAULT NULL COMMENT 'target info, json field',
    payment_config     json           DEFAULT NULL COMMENT 'payment_config snapshot',
    settle_time        datetime       DEFAULT NULL COMMENT 'settle time',
    payment_response   json           DEFAULT NULL COMMENT 'json payment response result',
    created_time       datetime       DEFAULT NULL COMMENT 'created time',
    updated_time       datetime       DEFAULT NULL COMMENT 'updated time',
    version            tinyint        DEFAULT '1' COMMENT 'optimistic locking',
    del_flag           tinyint(1)     DEFAULT '0' COMMENT '0-normal，1-delete',
    remark             varchar(200)   DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY udx_unique_id (unique_id) USING BTREE,
    KEY idx_account_id (account_id) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT ='transaction_payout';