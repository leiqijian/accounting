CREATE TABLE service_accounting_statement.account_transfer_config
(
    id                bigint unsigned  NOT NULL AUTO_INCREMENT COMMENT 'PK ID',
    merchant_id       bigint unsigned  NOT NULL DEFAULT '0' COMMENT 'merchant id',
    payin_account_id  bigint unsigned  NOT NULL DEFAULT '0' COMMENT 'disbursement account id, reference merchant PAY-IN accountId',
    payout_account_id bigint unsigned  NOT NULL DEFAULT '0' COMMENT 'income account id, reference merchant PAY-OUT accountId',
    operate_mode      tinyint unsigned NOT NULL DEFAULT '1' COMMENT 'operate mode: 0:AUTO, 1:MANUAL',
    state             tinyint unsigned NOT NULL DEFAULT '0' COMMENT 'status: 0:DISABLE, 1:ENABLE',
    created_time      datetime         NOT NULL COMMENT 'create time utc0',
    updated_time      datetime         NOT NULL COMMENT 'update time utc0',
    created_by        bigint unsigned  NULL     DEFAULT '0' COMMENT 'created by',
    updated_by        bigint unsigned  NULL     DEFAULT '0' COMMENT 'updated by',
    version           int unsigned     NOT NULL DEFAULT '1' COMMENT 'optimistic locking',
    del_flag          tinyint unsigned NOT NULL DEFAULT '0' COMMENT 'delete state: 0-normal,1-delete',
    remark            varchar(200)              DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id),
    UNIQUE KEY udx_transfer (merchant_id, payin_account_id, payout_account_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='internal payin/out account transfer config';

CREATE TABLE service_accounting_statement.account_transfer_record
(
    id                 bigint unsigned         NOT NULL AUTO_INCREMENT COMMENT 'PK ID',
    merchant_id        bigint unsigned         NOT NULL DEFAULT '0' COMMENT 'merchant id',
    payin_account_id   bigint unsigned         NOT NULL DEFAULT '0' COMMENT 'disbursement account id, reference merchant PAY-IN accountId',
    payout_account_id  bigint unsigned         NOT NULL DEFAULT '0' COMMENT 'income account id, reference merchant PAY-OUT accountId',
    transaction_amount decimal(26, 0) unsigned NOT NULL DEFAULT '0' COMMENT 'transfer amount, unit:cent',
    transaction_time   datetime                NOT NULL COMMENT 'transaction time utc0',
    operate_mode       tinyint unsigned        NOT NULL DEFAULT '0' COMMENT 'operate mode: 0:AUTO, 1:MANUAL',
    created_time       datetime                NOT NULL COMMENT 'create time utc0',
    updated_time       datetime                NOT NULL COMMENT 'update time utc0',
    version            int unsigned            NOT NULL DEFAULT '1' COMMENT 'optimistic locking',
    del_flag           tinyint unsigned        NOT NULL DEFAULT '0' COMMENT 'delete state: 0-normal,1-delete',
    remark             varchar(200)                     DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id),
    KEY idx_transfer (merchant_id, payin_account_id, payout_account_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='internal payin/out account transfer record';


ALTER TABLE service_accounting_statement.biz_transfer_out
    CHANGE COLUMN `payment_action` `operate_mode` tinyint unsigned NULL DEFAULT 0 COMMENT 'operate mode 0: AUTO, 1:MANUAL' AFTER payment_channel;


ALTER TABLE service_accounting_statement.biz_topup
    ADD COLUMN operate_mode tinyint unsigned NULL DEFAULT 0 COMMENT 'operate mode: 0:AUTO, 1:MANUAL' AFTER transaction_type_code;



