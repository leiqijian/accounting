CREATE TABLE service_accounting_statement.daily_exchange_rate
(
    id              bigint UNSIGNED  NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    merchant_id     bigint           NOT NULL COMMENT 'merchantId ref: merchant.id',
    account_id      bigint           NOT NULL COMMENT 'accountId ref: account.id',
    source_currency varchar(3)       NOT NULL COMMENT 'exchange from currency',
    target_currency varchar(3)       NOT NULL COMMENT 'exchange to currency',
    exchange_date   date             NOT NULL COMMENT 'exchange to date',
    exchange_rate   decimal(26, 6)   NOT NULL COMMENT 'day of exchange to rate',
    ratio_lose      decimal(26, 6)   NULL DEFAULT NULL COMMENT 'merchant ratio lose',
    merchant_rate   decimal(26, 6)   NOT NULL COMMENT 'merchant_rate=exchange_rate * (1-ratio_lose)',
    created_time    datetime         NULL DEFAULT NULL,
    updated_time    datetime         NULL DEFAULT NULL,
    version         int              NULL DEFAULT 1 COMMENT 'optimistic locking',
    del_flag        tinyint UNSIGNED NULL DEFAULT 0 COMMENT '0-normal,1-delete',
    remark          varchar(200)     NULL DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    INDEX idx_merchant_account_id (merchant_id, account_id) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT ='daily exchange rate';

ALTER TABLE service_accounting_statement.daily_exchange_rate
    ADD UNIQUE INDEX udx_unique_key(merchant_id, account_id, source_currency, target_currency, exchange_date) USING BTREE;

UPDATE service_accounting_statement.account SET source_currency = 'USD' WHERE source_currency IS NULL;

ALTER TABLE service_accounting_statement.account_daily_bill
    ADD COLUMN transaction_amount_usd decimal(26) NULL DEFAULT 0 COMMENT 'daily transaction amount(USD)' AFTER transaction_amount;
