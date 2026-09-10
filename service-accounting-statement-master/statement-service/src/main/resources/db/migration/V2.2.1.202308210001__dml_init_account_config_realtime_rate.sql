INSERT INTO service_accounting_statement.account_config (merchant_id, account_id, config_data)
SELECT merchant_id, id,'{"calculateConfig":{"realTimeRate":false}}'
FROM account WHERE id NOT IN (SELECT account_id FROM account_config);