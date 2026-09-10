ALTER TABLE `service_accounting_statement`.`transaction_money`
    ADD COLUMN `trading_model` varchar(6) NULL DEFAULT 'D+0' COMMENT 'just payin account trading model used D+1, D+2..., T+1, T+2...; all payout account trading model used D+0 or T+0;' AFTER `settle_status`;

/*
UPDATE service_accounting_statement.transaction_money tm
LEFT JOIN service_accounting_base.account_product ap ON (tm.account_id = ap.account_id
    AND tm.product_code = ap.product_code)
SET tm.trading_model = ap.trading_model
WHERE tm.transaction_type_code = 'PAY_IN'
AND tm.trading_model = 'D+0'
;

UPDATE service_accounting_statement.transaction_money tm
LEFT JOIN service_accounting_statement.account_daily_init di ON (tm.bill_id = di.id)
SET tm.be_credited_date = di.transaction_date
WHERE tm.transaction_type_code = 'PAY_OUT'
AND tm.be_credited_date IS NULL
;
*/