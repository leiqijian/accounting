ALTER TABLE `service_accounting_statement`.`report_config`
    CHANGE COLUMN `protocol` `upload_protocol` varchar (32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci
    NULL DEFAULT NULL COMMENT 'FTP/S3' AFTER `compress`;