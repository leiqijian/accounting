-- service_accounting_worker
-- CREATE DATABASE IF NOT EXISTS service_accounting_worker DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
-- USE service_accounting_worker;


CREATE TABLE daily_exchange_rate
(
    id              bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    merchant_id     bigint         NOT NULL COMMENT 'merchantId ref: merchant.id',
    account_id      bigint         NOT NULL COMMENT 'accountId ref: account.id',
    source_currency varchar(3)     NOT NULL COMMENT 'exchange from currency',
    target_currency varchar(3)     NOT NULL COMMENT 'exchange to currency',
    exchange_date   date           NOT NULL COMMENT 'exchange to date',
    exchange_rate   decimal(26, 6) NOT NULL COMMENT 'day of exchange to rate',
    ratio_lose      decimal(26, 6) NULL DEFAULT NULL COMMENT 'merchant ratio lose',
    merchant_rate   decimal(26, 6) NOT NULL COMMENT 'merchant_rate=exchange_rate * (1-ratio_lose)',
    created_time    datetime NULL DEFAULT NULL,
    updated_time    datetime NULL DEFAULT NULL,
    version         int NULL DEFAULT 1 COMMENT 'optimistic locking',
    del_flag        tinyint UNSIGNED NULL DEFAULT 0 COMMENT '0-normal,1-delete',
    remark          varchar(200) NULL DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    INDEX           idx_merchant_account_id(merchant_id, account_id) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


CREATE TABLE task_fee_calculation
(
    id                    bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    unique_id             varchar(128)   NOT NULL COMMENT 'from transaction service uniqueId',
    task_type             varchar(32)    NOT NULL DEFAULT 'UNREPEATABLE' COMMENT 'TaskTypeEnum: UNREPEATABLE/REPEATABLE',
    task_status           varchar(32)    NOT NULL DEFAULT 'WAITING' COMMENT 'StatusEnum: WAITING/PROCESSING/SUCCESS/FAILED',
    merchant_code         varchar(128)   NOT NULL COMMENT 'from transaction system',
    merchant_name         varchar(128) NULL DEFAULT NULL,
    country_code          varchar(2)     NOT NULL COMMENT 'CountryCodeEnum',
    transaction_type_code varchar(32)    NOT NULL COMMENT 'TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS',
    product_code          varchar(32)    NOT NULL COMMENT 'ProductCodeEnum',
    merchant_reference    varchar(128) NULL DEFAULT NULL COMMENT 'idempotent key',
    transaction_time      timestamp NULL DEFAULT NULL,
    amount                decimal(26, 0) NOT NULL,
    currency              varchar(3)     NOT NULL,
    transaction_status    varchar(32)    NOT NULL COMMENT 'TransactionStatusEnum: IN_PROGRESS/SETTLED/FAILED/CHARGED_BACK/REFUNDING/REFUNDED/EXPIRED',
    direction_type        varchar(32)    NOT NULL DEFAULT '' COMMENT 'DirectionTypeEnum: SETTLED/REFUND/CHARGE_BACK',
    comments              varchar(400) NULL DEFAULT NULL,
    `others`              json NULL COMMENT 'json extend data',
    event_time            timestamp      NOT NULL,
    created_time          datetime NULL DEFAULT NULL,
    updated_time          datetime NULL DEFAULT NULL,
    created_by            bigint NULL DEFAULT 0,
    updated_by            bigint NULL DEFAULT 0,
    version               int            NOT NULL DEFAULT 0 COMMENT 'optimistic locking 0:normal, 1:locked',
    del_flag              tinyint(1) NOT NULL DEFAULT 0 COMMENT '0-normal，1-delete',
    remark                varchar(200) NULL DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE INDEX task_fee_calculation_unique_id_uindex(unique_id) USING BTREE,
    INDEX                 task_fee_calculation_event_time_index(event_time) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'task_fee_calculation' ROW_FORMAT = Dynamic;


CREATE TABLE task_fee_order_flow
(
    id                    bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    unique_id             varchar(128)   NOT NULL,
    task_type             varchar(32)    NOT NULL DEFAULT 'UNREPEATABLE' COMMENT 'TaskTypeEnum: UNREPEATABLE/REPEATABLE',
    merchant_code         varchar(128)   NOT NULL COMMENT 'from transaction system',
    merchant_name         varchar(128) NULL DEFAULT NULL,
    country_code          varchar(2)     NOT NULL COMMENT 'CountryCodeEnum',
    transaction_type_code varchar(32)    NOT NULL COMMENT 'TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS',
    product_code          varchar(32)    NOT NULL COMMENT 'ProductCodeEnum',
    merchant_reference    varchar(128) NULL DEFAULT NULL COMMENT 'idempotent key',
    transaction_time      timestamp NULL DEFAULT NULL,
    amount                decimal(26, 0) NOT NULL,
    currency              varchar(3)     NOT NULL,
    transaction_status    varchar(32)    NOT NULL COMMENT 'TransactionStatusEnum: IN_PROGRESS/SETTLED/FAILED/CHARGED_BACK/REFUNDING/REFUNDED/EXPIRED',
    direction_type        varchar(32)    NOT NULL DEFAULT '' COMMENT 'DirectionTypeEnum: SETTLED/REFUND/CHARGE_BACK',
    comments              varchar(400) NULL DEFAULT NULL,
    others                json NULL,
    event_time            timestamp      NOT NULL,
    created_time          datetime       NOT NULL,
    remark                varchar(200) NULL DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'task_fee_order_flow' ROW_FORMAT = Dynamic;


CREATE TABLE task_log_fee_calculation
(
    id            bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    task_id       bigint NULL DEFAULT NULL COMMENT 'fk: task_fee_calculation.id',
    start_time    timestamp NULL DEFAULT NULL,
    end_time      timestamp NULL DEFAULT NULL,
    task_result   varchar(32) NULL DEFAULT NULL COMMENT 'TaskResultEnum: FAILED/SUCCESS',
    execution_log text NULL,
    created_time  datetime NULL DEFAULT NULL,
    updated_time  datetime NULL DEFAULT NULL,
    remark        varchar(200) NULL DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    INDEX         idx_task_id(task_id) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'task_log_fee_calculation' ROW_FORMAT = Dynamic;

