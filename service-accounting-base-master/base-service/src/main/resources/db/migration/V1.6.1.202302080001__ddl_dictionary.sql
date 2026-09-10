CREATE TABLE service_accounting_base.dictionary
(
    id                 bigint unsigned         NOT NULL AUTO_INCREMENT COMMENT 'Primary key Id',
    dict_type          varchar(20)             NOT NULL COMMENT 'Dictionary type',
    dict_key           varchar(50)             NOT NULL COMMENT 'Dictionary key',
    dict_value         varchar(200)            NOT NULL COMMENT 'Dictionary value',
    del_flag           tinyint(1)              NOT NULL DEFAULT '0' COMMENT 'Del flag 0:normal, 1:delete',
    PRIMARY KEY (id),
    UNIQUE INDEX udx_type_key(dict_type, dict_key) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='dictionary';