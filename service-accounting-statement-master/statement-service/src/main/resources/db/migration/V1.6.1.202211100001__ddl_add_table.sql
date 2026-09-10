CREATE TABLE service_accounting_statement.global_account
(
    id             bigint unsigned      NOT NULL AUTO_INCREMENT COMMENT 'PK id',
    merchant_id    bigint unsigned      NOT NULL COMMENT 'merchant id',
    global_balance decimal(26) unsigned NOT NULL DEFAULT '0' COMMENT 'global account balance, unit:cent',
    currency       varchar(3)           NOT NULL DEFAULT '' COMMENT 'currency: USD, MXN, BRL',
    created_time   datetime             NOT NULL COMMENT 'created time',
    updated_time   datetime             NOT NULL COMMENT 'updated time',
    version        bigint unsigned      NOT NULL DEFAULT '1' COMMENT 'optimistic locking',
    del_flag       tinyint unsigned     NOT NULL DEFAULT '0' COMMENT 'delete state: 0-normal,1-delete',
    remark         varchar(200)                  DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id),
    UNIQUE KEY udx_merchant_id (merchant_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='global account info';


CREATE TABLE service_accounting_statement.global_statement
(
    id            bigint unsigned      NOT NULL AUTO_INCREMENT COMMENT 'PK id',
    global_id     bigint               NOT NULL COMMENT 'group account id, ref: global_account.id',
    account_id    bigint unsigned      NOT NULL DEFAULT '0' COMMENT 'topup or transfer_out to target sub account id, when trade_type=SELF then account_id=0',
    business_type varchar(32)          NOT NULL DEFAULT '' COMMENT 'business_type :TOPUP, TRANSFER_OUT',
    trade_type    varchar(8)           NOT NULL DEFAULT '' COMMENT 'trade_type: SELF:self account, SUB:sub_account',
    amount        decimal(26) unsigned NOT NULL DEFAULT '0' COMMENT 'transaction amount, unit:cent',
    start_balance decimal(26)          NOT NULL DEFAULT '0' COMMENT 'start balance, unit:cent',
    end_balance   decimal(26)          NOT NULL DEFAULT '0' COMMENT 'end balance, unit:cent',
    currency      varchar(3)           NOT NULL DEFAULT '' COMMENT 'currency: USD, MXN, BRL',
    created_time  datetime             NOT NULL COMMENT 'created time',
    updated_time  datetime             NOT NULL COMMENT 'updated time',
    version       bigint unsigned      NOT NULL DEFAULT '1' COMMENT 'optimistic locking',
    del_flag      tinyint unsigned     NOT NULL DEFAULT '0' COMMENT 'delete state: 0-normal,1-delete',
    remark        varchar(200)                  DEFAULT NULL COMMENT 'remark',
    PRIMARY KEY (id),
    KEY idx_merchant_id (global_id),
    KEY idx_account_id (account_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='global account statement flow';