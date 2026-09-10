CREATE TABLE service_accounting_statement.transaction_unhold
(
    id              bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK ID',
    account_id      bigint     NOT NULL COMMENT 'accountId',
    bill_id         bigint     NOT NULL COMMENT 'billId',
    unhold_date     date       NOT NULL COMMENT 'Unhold date: convert to account timezone',
    unhold_time     datetime   NOT NULL COMMENT 'Untold datetime(UTC+0)',
    unhold_amount   decimal(26, 0) UNSIGNED NULL DEFAULT 0 COMMENT 'total unhold amount',
    unfreeze_amount decimal(26, 0) UNSIGNED NULL DEFAULT 0 COMMENT 'can be add to extractable balance amount',
    currency        varchar(3) NOT NULL COMMENT 'currency',
    unhold_list     json       NOT NULL COMMENT 'this batch unhold total transactionIds',
    unfreeze_list   json       NOT NULL COMMENT 'this batch can be add to extractable balance transactionIds',
    created_time    datetime   NOT NULL COMMENT 'created time',
    updated_time    datetime   NOT NULL COMMENT 'updated time',
    created_by      bigint     NOT NULL DEFAULT 0 COMMENT 'created by',
    updated_by      bigint     NOT NULL DEFAULT 0 COMMENT 'updated by',
    version         tinyint(4) NOT NULL DEFAULT 1 COMMENT 'optimistic locking',
    del_flag        tinyint(1) NULL DEFAULT 0 COMMENT 'delete state: 0:normal, 1:delete',
    remark          varchar(200) NULL DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    INDEX           idx_account_unhold_bill(account_id, bill_id) USING BTREE,
    INDEX           idx_account_unhold_date(account_id, unhold_date) USING BTREE
)ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'transaction unhold' ROW_FORMAT = DYNAMIC;


CREATE TABLE service_accounting_statement.account_statement_biz
(
    id                 bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK ID',
    request_id         varchar(64)    NOT NULL COMMENT 'requestId unique',
    transaction_id     bigint UNSIGNED NOT NULL COMMENT 'transactionId',
    merchant_id        bigint UNSIGNED NOT NULL COMMENT 'merchantId',
    account_id         bigint UNSIGNED NOT NULL COMMENT 'accountId',
    bill_id            bigint UNSIGNED NOT NULL COMMENT 'billId',
    business_type      varchar(32)    NOT NULL COMMENT 'business type: TOPUP, TRANSFER_OUT, EXCHANGE ...',
    finance_type       varchar(32)    NOT NULL COMMENT 'finance type: PRE_FREEZING, TRANSACTION_DEAL, UNFREEZE',
    transaction_time   datetime       NOT NULL COMMENT 'transaction time now(UTC+0)',
    extractable_amount decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'extractable amount, unit:cent',
    frozen_amount      decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'frozen amount, unit:cent',
    exchange_amount    decimal(26, 0) NOT NULL DEFAULT 0 COMMENT 'exchange amount, unit:cent',
    currency           varchar(3)     NOT NULL COMMENT ' currency USD, MXN, BRL ...',
    created_time       datetime       NOT NULL COMMENT 'created time',
    updated_time       datetime       NOT NULL COMMENT 'updated time',
    created_by         bigint         NOT NULL DEFAULT 0 COMMENT 'created by',
    updated_by         bigint         NOT NULL DEFAULT 0 COMMENT 'updated by',
    version            tinyint(4) NOT NULL DEFAULT 1 COMMENT 'optimistic locking',
    del_flag           tinyint(1) NULL DEFAULT 0 COMMENT 'delete state: 0:normal, 1:delete',
    remark             varchar(200) NULL DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE INDEX udx_request_id(request_id) USING BTREE,
    UNIQUE INDEX udx_transaction_id(transaction_id,account_id,business_type,finance_type) USING BTREE,
    INDEX              idx_account_bill_id(account_id, bill_id) USING BTREE,
    INDEX              idx_account_transaction_time(account_id, transaction_time) USING BTREE
)ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'account statement biz' ROW_FORMAT = DYNAMIC;
