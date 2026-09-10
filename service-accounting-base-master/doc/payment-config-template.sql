CREATE TABLE payment_config
(
    id           bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'primary key Id',
    account_id   bigint         NOT NULL COMMENT 'ref:account.id',
    product_code varchar(32)    NOT NULL COMMENT 'product code',
    max_amount   decimal(26, 0) NOT NULL COMMENT 'single maximum transaction amount, unit:cent',
    api_key      varchar(128)   NOT NULL COMMENT 'x-api-key',
    json_params  varchar(800) NULL DEFAULT NULL COMMENT 'Json Dynamic expansion parameters',
    created_time datetime NULL DEFAULT NULL COMMENT 'created_time',
    updated_time datetime NULL DEFAULT NULL COMMENT 'last update time',
    version      int UNSIGNED NULL DEFAULT 1 COMMENT 'optimistic locking',
    status       tinyint NULL DEFAULT 0 COMMENT '0-disable, 1-enable',
    del_flag     tinyint(1) NULL DEFAULT 0 COMMENT '0-normal, 1-delete',
    remark       varchar(200) NULL DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100617826678386602 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'payment_config' ROW_FORMAT = Dynamic;

-- auto payment config template
INSERT INTO payment_config
set id=100617826678386600,
    account_id=13864700725755914,
    product_code='SPEI',
    max_amount=100000000,
    api_key='jGP3XQKFBm2YUAChUzqzh8go5CLXZ9cF3SPUHoOX',
    json_params='{"targetName": "TEST-DIDI PAY SA DE CV", "targetBankAccountId": "002180903699299247"}',
    created_time=now(),
    updated_time=now(),
    version=1,
    status=1,
    del_flag=0,
    remark='CHEN FAN AUTO PAYMENT';


