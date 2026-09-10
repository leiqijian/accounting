ALTER TABLE `service_accounting_statement`.`account_daily_bill`
    ADD COLUMN `adjustment_amount` decimal(26, 0) NULL DEFAULT 0 COMMENT 'daily adjustment total amount, unit:cent' AFTER `refund_tax`,
    ADD COLUMN `adjustment_count`  int            NULL DEFAULT 0 COMMENT 'daily adjustment total count' AFTER `adjustment_amount`;

ALTER TABLE `service_accounting_statement`.`transaction_money`
    MODIFY COLUMN `fx_rate` decimal(26, 6) NULL DEFAULT 1.0 COMMENT 'Exchange rate from order to account currency' AFTER `currency`,
    ADD COLUMN `vendor`      varchar(32)    NULL DEFAULT '' COMMENT 'Payment channel supplier' AFTER `document_id`,
    ADD COLUMN `fx_rate_id`  bigint         NULL DEFAULT '0' COMMENT 'fx exchange rate config id, ref: daily_exchange_rate.id' AFTER `vendor`,
    ADD COLUMN `fx_rate_usd` decimal(26, 6) NULL DEFAULT 1.0 COMMENT 'Exchange rate from order to USD' AFTER `settlement_amount_usd`,
    ADD COLUMN `fx_lose_usd` decimal(26, 6) NULL DEFAULT 0.0 COMMENT 'Exchange lose from order to USD' AFTER `fx_rate_usd`;

ALTER TABLE `service_accounting_statement`.`transaction_fee`
    ADD COLUMN `settlement_amount_usd` decimal(26, 6) NULL DEFAULT 0 COMMENT 'Fee currency converted to USD, unit: cent' AFTER `settlement_currency`;