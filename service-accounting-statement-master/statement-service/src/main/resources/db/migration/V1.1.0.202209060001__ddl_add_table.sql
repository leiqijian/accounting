CREATE TABLE service_accounting_statement.account_daily_init
(
    id               bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    account_id       bigint          NOT NULL COMMENT 'fk, account.id',
    transaction_date date            NOT NULL COMMENT 'transaction date yyyy-MM-dd',
    version          int          DEFAULT '1' COMMENT 'optimistic locking',
    remark           varchar(200) DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE,
    UNIQUE KEY udx_account_bill_date (account_id, transaction_date) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='account_daily_init';

--  init data
INSERT INTO service_accounting_statement.account_daily_init(id, account_id, transaction_date, version, remark)
SELECT id, account_id, bill_date, version, remark
FROM service_accounting_statement.account_daily_bill;