ALTER TABLE service_accounting_statement.report_config
    ADD COLUMN report_strategy varchar(20) NULL DEFAULT 'GENERIC' COMMENT 'report strategy: GENERIC, CUSTOMIZED; default:GENERIC' AFTER merchant_code;

/* cheng_fan, wan_bo, dukpay, liquido USE GENERIC strategy */
UPDATE service_accounting_statement.report_config SET report_strategy='GENERIC'
WHERE merchant_id in(83862785170341883,83862785170341884,83862785170341885,83862785170341900);

/* didi, kwai USE CUSTOMIZED strategy */
UPDATE service_accounting_statement.report_config SET report_strategy='CUSTOMIZED'
WHERE merchant_id in(83862785170341881,83862785170341882);
