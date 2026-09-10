UPDATE service_accounting_statement.transaction_payout
SET target_info = REPLACE(target_info, 'targetPixKey', 'targetBankAccountId')
where country_code = 'BR'
  AND payment_channel = 'PIX';

UPDATE service_accounting_statement.transaction_payout
SET target_info = REPLACE(target_info, 'targetPixKeyType', 'targetBankAccountType')
where country_code = 'BR'
  AND payment_channel = 'PIX';
