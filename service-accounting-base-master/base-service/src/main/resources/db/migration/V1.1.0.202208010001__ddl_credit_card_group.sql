/* reference: https://www.bindb.com/card-schemes  */
DROP TABLE IF EXISTS credit_card_group;
CREATE TABLE credit_card_group
(
    id         bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK id',
    group_name varchar(40)     NOT NULL DEFAULT 'Default' COMMENT 'credit card group name',
    group_code varchar(40)     NOT NULL DEFAULT 'DEFAULT' COMMENT 'credit card group code',
    inn_begin  int             NULL     DEFAULT '0' COMMENT 'inn range begin',
    inn_end    int             NULL     DEFAULT '0' COMMENT 'inn range end',
    remark     varchar(255)    NULL     DEFAULT '' COMMENT 'remark',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='credit card group';

/* DEFAULT */
INSERT INTO credit_card_group
VALUES (127402080430588000, 'Default', 'DEFAULT', 0, 0, 'Default config');

/* Visa: IIN range 400000-499999 with primary account number (Pan) length 16 */
INSERT INTO credit_card_group
VALUES (127402080430588001, 'Visa', 'VISA', 400000, 499999, '');

/* MasterCard: IIN ranges are 222100-272099 and 510000-559999 both with primary account number (Pan) length 16.*/
INSERT INTO credit_card_group
VALUES (127402080430588002, 'MasterCard', 'MASTER_CARD', 222100, 272099, ''),
       (127402080430588003, 'MasterCard', 'MASTER_CARD', 510000, 559999, '');

/* Maestro: IIN ranges varies 50, 56-69, 630000-630000, 670000-679999 and pan varies 16-19 */
INSERT INTO credit_card_group
VALUES (127402080430588004, 'Maestro', 'MAESTRO', 50, 50, ''),
       (127402080430588005, 'Maestro', 'MAESTRO', 56, 69, ''),
       (127402080430588006, 'Maestro', 'MAESTRO', 630000, 630000, ''),
       (127402080430588007, 'Maestro', 'MAESTRO', 670000, 679999, '');

/* AmericanExpress: Card ranges are 340000-349999 and 370000-379999 and pan length is 15 digits. */
INSERT INTO credit_card_group
VALUES (127402080430588008, 'AmericanExpress', 'AMERICAN_EXPRESS', 340000, 349999, ''),
       (127402080430588009, 'AmericanExpress', 'AMERICAN_EXPRESS', 370000, 379999, '');

/* DiscoverCards: IIN Ranges are 601100-601103,601105-601109, 601120-601149, 601174-601174, 601177-601179, 601186-601199, 644000-659999 */
INSERT INTO credit_card_group
VALUES (127402080430588010, 'DiscoverCards', 'DISCOVER_CARDS', 601100, 601103, ''),
       (127402080430588011, 'DiscoverCards', 'DISCOVER_CARDS', 601105, 601109, ''),
       (127402080430588012, 'DiscoverCards', 'DISCOVER_CARDS', 601120, 601149, ''),
       (127402080430588013, 'DiscoverCards', 'DISCOVER_CARDS', 601174, 601174, ''),
       (127402080430588014, 'DiscoverCards', 'DISCOVER_CARDS', 601177, 601179, ''),
       (127402080430588015, 'DiscoverCards', 'DISCOVER_CARDS', 601186, 601199, ''),
       (127402080430588016, 'DiscoverCards', 'DISCOVER_CARDS', 644000, 659999, '');

/* DinersClubInternational: IIN Ranges are 300000-305999, 309500-309599, 360000-369999, 380000-399999 */
INSERT INTO credit_card_group
VALUES (127402080430588017, 'DinersClubInternational', 'DCI', 300000, 305999, ''),
       (127402080430588018, 'DinersClubInternational', 'DCI', 309500, 309599, ''),
       (127402080430588019, 'DinersClubInternational', 'DCI', 360000, 369999, ''),
       (127402080430588020, 'DinersClubInternational', 'DCI', 380000, 399999, '');

/* UnionPay: IIN Ranges are 622126-622925, 624000-626999, 628200-628899, 810000-817199, */
INSERT INTO credit_card_group
VALUES (127402080430588021, 'UnionPay', 'UNION_PAY', 622126, 622925, ''),
       (127402080430588022, 'UnionPay', 'UNION_PAY', 624000, 626999, ''),
       (127402080430588023, 'UnionPay', 'UNION_PAY', 628200, 628899, ''),
       (127402080430588024, 'UnionPay', 'UNION_PAY', 810000, 817199, '');

/* Brazil Elo: ????? */
INSERT INTO credit_card_group
VALUES (127402080430588025, 'Elo', 'ELO', 0, 0, '');


