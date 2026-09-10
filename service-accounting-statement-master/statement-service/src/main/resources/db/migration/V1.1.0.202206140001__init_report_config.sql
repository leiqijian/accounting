CREATE TABLE `report_config`  (
    `id` bigint NOT NULL COMMENT 'snowflake Id',
    `merchant_id` bigint NULL DEFAULT NULL,
    `file_type` varchar(32) NULL DEFAULT NULL COMMENT 'CSV/XLS/XLSX',
    `compress` tinyint(1) NULL DEFAULT NULL COMMENT '0-normal，1-compress',
    `protocol` varchar(32) NULL DEFAULT NULL COMMENT 'FTP/S3',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_merchant_id`(`merchant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

INSERT INTO `report_config` VALUES
(1, 83862785170341881, 'CSV', 1, 'FTP'),
(2, 83862785170341882, 'CSV', 0, 'FTP'),
(3, 83862785170341883, 'XLSX', 0, 'S3'),
(4, 83862785170341884, 'XLSX', 0, 'S3'),
(5, 83862785170341885, 'XLSX', 0, 'S3');
