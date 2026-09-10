CREATE TABLE service_accounting_statement.accounting_schedule
(
    id                     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK Id',
    merchant_id            BIGINT UNSIGNED NOT NULL COMMENT 'merchant_id',
    account_id             BIGINT UNSIGNED NOT NULL COMMENT 'account_id',
    transaction_id         BIGINT UNSIGNED NOT NULL COMMENT 'transaction_id',
    unique_id              VARCHAR(128) NOT NULL COMMENT 'unique_id',
    transaction_date       DATE         NOT NULL COMMENT 'transaction date (in account timezone)',
    accounting_date        DATE         NOT NULL COMMENT 'accounting schedule date (in account timezone)',
    accounting_amount      DECIMAL(26, 0) UNSIGNED NOT NULL DEFAULT '0' COMMENT 'accounting_amount, unit:cent',
    current_installment    TINYINT ( 4 ) UNSIGNED DEFAULT '0' COMMENT 'current installment',
    total_installment      TINYINT ( 4 ) UNSIGNED DEFAULT '0' COMMENT 'total_installment',
    state                  TINYINT ( 4 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT 'state; 0:pending, 1:accounted, 2: holding',
    actual_accounting_time DATETIME NULL COMMENT 'actual accounting time UTC+0',
    created_time           DATETIME DEFAULT NULL,
    updated_time           DATETIME DEFAULT NULL,
    version                BIGINT UNSIGNED DEFAULT '1' COMMENT 'optimistic locking',
    del_flag               TINYINT ( 1 ) NOT NULL DEFAULT '0' COMMENT '0-normal，1-delete',
    remark                 VARCHAR(200) NULL DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    KEY                    idx_accounting_date ( account_id,`state`, accounting_date) USING BTREE,
    KEY                    idx_transaction_id ( transaction_id ) USING BTREE,
    KEY                    idx_unique_id ( unique_id ) USING BTREE
) ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'accounting schedule';