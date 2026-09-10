-- merchant
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant`  (
    `id`            bigint UNSIGNED NOT NULL            AUTO_INCREMENT COMMENT 'primary key Id',
    `code`          varchar(128)    NOT NULL            COMMENT 'Merchant code',
    `uuid`          varchar(64)     NOT NULL,
    `name`          varchar(128)    NOT NULL DEFAULT '' COMMENT 'merchant name',
    `short_name`    varchar(128)    NULL DEFAULT ''     COMMENT 'merchant short name',
    `logo_icon`     varchar(255)    NULL DEFAULT ''     COMMENT 'merchant icon',
    `created_time`  datetime        NULL DEFAULT NULL   COMMENT 'created time',
    `updated_time`  datetime        NULL DEFAULT NULL   COMMENT 'updated time',
    `created_by`    bigint          NULL DEFAULT 0      COMMENT 'created by',
    `updated_by`    bigint          NULL DEFAULT 0      COMMENT 'updated by',
    `version`       int             NULL DEFAULT 0      COMMENT 'optimistic locking flag',
    `del_flag`      tinyint(1)      NULL DEFAULT 0      COMMENT 'delFlag: 0-normal, 1-delete',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 83862785170341902 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'merchant info' ROW_FORMAT = Dynamic;


INSERT INTO `merchant` VALUES
(83862785170341881, 'kwai', 'a8669022-c72b-4f8d-a926-29da8fa1ea5f', 'Joyo Technology Pte, Ltd.', 'Kwai', 'https://accounting-service-daily-bill-dev.s3.ap-southeast-1.amazonaws.com/web/kwai.webp', '2022-05-31 12:24:29', '2022-05-31 12:24:29', 0, 0, 0, 0),
(83862785170341882, 'didicredit', 'bec26a4d-b699-413f-981a-c6909b3a743e', 'DIDI PAY, S.A. DE C.V. ', 'DiDi', 'https://accounting-service-daily-bill-dev.s3.ap-southeast-1.amazonaws.com/web/didi.webp', '2022-05-31 12:24:29', '2022-05-31 12:24:29', 0, 0, 0, 0),
(83862785170341883, 'cheng_fan', 'c60e08b4-27ac-4227-8b16-75655f9d65d8', 'X6 Technology Pte, Ltd.', 'X6', '', '2022-05-31 12:24:29', '2022-05-31 12:24:29', 0, 0, 0, 0),
(83862785170341884, 'wan_bo', 'dba1be4b-6d43-4e20-82c5-4cc1584d5537', 'MILEXCESS INDUSTRY LIMITED', 'MILEXCESS', '', '2022-05-31 12:24:29', '2022-05-31 12:24:29', 0, 0, 0, 0),
(83862785170341885, 'dukpay', 'eb9a4359-e3a1-4c5b-b674-8440312efa5a', 'DUKPAY LIMITED', 'DuKPay', 'https://accounting-service-daily-bill-dev.s3.ap-southeast-1.amazonaws.com/web/DUK.jpeg', '2022-06-10 04:18:53', '2022-06-10 04:18:53', 0, 0, 0, 0),
(83862785170341886, 'calii', 'fcab2c6b-a71f-4f40-aa2a-a18a26d1101d', 'Calii', 'Calii', NULL, '2022-07-13 09:20:55', '2022-07-13 09:20:55', 0, 0, 0, 0),
(83862785170341887, 'gee_wallet', 'g3ced52d-7754-4870-99cb-d305728880a3', 'GeeWallet', 'GeeWallet', NULL, '2022-07-27 12:02:13', '2022-07-27 12:02:13', 0, 0, 0, 0),
(83862785170341900, 'liquido', '0f2f0a50-9c40-4f65-b39b-3a5ded49f2fb', 'Liquido', 'Liquido', 'https://accounting-service-daily-bill-dev.s3.ap-southeast-1.amazonaws.com/web/liquido.png', '2022-06-28 06:48:46', '2022-06-28 06:48:46', 0, 0, 0, 0),
(83862785170341901, 'cb_inter', '05f27426-2af4-472b-aa8a-f3805c24857c', 'CB_INTRT', 'CbInter', NULL, '2022-07-13 09:15:07', '2022-07-13 09:15:07', 0, 0, 0, 0);





-- merchant_message
DROP TABLE IF EXISTS `merchant_message`;
CREATE TABLE `merchant_message`  (
    `id`                    bigint      NOT NULL            COMMENT 'id',
    `merchant_id`           bigint      NOT NULL            COMMENT 'merchant id',
    `country_code`          varchar(2)  NOT NULL            COMMENT 'CountryCodeEnum',
    `message_source_type`   varchar(32) NOT NULL            COMMENT 'message source type ',
    `message_type`          varchar(32) NOT NULL            COMMENT 'message type',
    `message`               text        NULL                COMMENT 'message',
    `event_time`            timestamp   NOT NULL            COMMENT 'event time',
    `del_flag`              tinyint(1)  NOT NULL DEFAULT 0  COMMENT 'delFlag: 0-normal, 1-delete',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_merchant_time`(`merchant_id` ASC, `country_code` ASC, `event_time` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

INSERT INTO `merchant_message` VALUES (1, 83862785170341883, 'BR', 'BILL', 'WARN', 'PAY_IN: REFUND: 2', '2022-06-28 09:01:58', 0);
