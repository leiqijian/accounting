CREATE TABLE service_accounting_worker.task_holding_monitor
(
    id           bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK, ID',
    monthly      int unsigned    NOT NULL DEFAULT '0' COMMENT 'belong month: yyyyMM',
    document_id  varchar(40)     NOT NULL DEFAULT '' COMMENT 'document id certificateNo, e.g: CPF, CNPJ',
    total_amount decimal(26, 0) unsigned  DEFAULT '0' COMMENT 'current month total amount,unit:cent',
    version      int unsigned             DEFAULT '1' COMMENT 'optimistic locking',
    remark       varchar(200)             DEFAULT '' COMMENT 'remark',
    created_time datetime                 DEFAULT NULL COMMENT 'created time utc0',
    updated_time datetime                 DEFAULT NULL COMMENT 'updated time utc0',
    PRIMARY KEY (id),
    UNIQUE KEY udx_monthly_document_id (monthly, document_id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='task holding monitor';

ALTER TABLE service_accounting_worker.task_fee_calculation
    ADD COLUMN document_id varchar(40)      NULL     DEFAULT '' COMMENT 'document id certificate No, e.g: CPF, CNPJ' AFTER unique_id,
    ADD COLUMN hold_status tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT 'hold status, 0:OFF, 1:ON, default:OFF' AFTER task_status;

