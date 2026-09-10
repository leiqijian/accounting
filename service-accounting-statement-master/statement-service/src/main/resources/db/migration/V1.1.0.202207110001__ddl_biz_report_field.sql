/* ADD/MODIFY COLUMN */
ALTER TABLE service_accounting_statement.biz_refund
MODIFY COLUMN transaction_time timestamp NULL DEFAULT NULL COMMENT 'initiate transaction time (UTC 0)' AFTER opposite_name,
MODIFY COLUMN amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'The transaction amount before the exchange rate transfer, unit:cent' AFTER transaction_type_code,
MODIFY COLUMN settlement_amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'Amount after exchange rate transfer (before deduction of fees and taxes),unit:cent' AFTER fx_rate,
ADD COLUMN settle_time timestamp NULL COMMENT 'after completed settlement time (UTC 0)' AFTER transaction_time;

ALTER TABLE service_accounting_statement.biz_topup
MODIFY COLUMN transaction_time timestamp NULL DEFAULT NULL COMMENT 'initiate transaction time (UTC 0)' AFTER account_id,
MODIFY COLUMN amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'The transaction amount before the exchange rate transfer, unit:cent' AFTER transaction_type_code,
MODIFY COLUMN settlement_amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'Amount after exchange rate transfer (before deduction of fees and taxes),unit:cent' AFTER fx_rate,
ADD COLUMN settle_time timestamp NULL COMMENT 'after completed settlement time (UTC 0)' AFTER transaction_time;

ALTER TABLE service_accounting_statement.biz_transfer_out
MODIFY COLUMN transaction_time timestamp NULL DEFAULT NULL COMMENT 'initiate transaction time (UTC 0)' AFTER account_id,
MODIFY COLUMN amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'The transaction amount before the exchange rate transfer, unit:cent' AFTER transaction_type_code,
MODIFY COLUMN settlement_amount decimal(26, 0) NULL DEFAULT 0 COMMENT 'Amount after exchange rate transfer (before deduction of fees and taxes),unit:cent' AFTER fx_rate,
ADD COLUMN settle_time timestamp NULL COMMENT 'after completed settlement time (UTC 0)' AFTER transaction_time;


/* UPDATE DATA */
UPDATE service_accounting_statement.biz_topup  SET settle_time = transaction_time;
UPDATE service_accounting_statement.biz_refund  SET settle_time = transaction_time;
UPDATE service_accounting_statement.biz_transfer_out  SET settle_time = transaction_time;