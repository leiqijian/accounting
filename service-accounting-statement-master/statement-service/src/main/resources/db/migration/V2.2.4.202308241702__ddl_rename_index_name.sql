ALTER TABLE `service_accounting_statement`.`transaction_charge_back_order`
    RENAME INDEX `idx_account_chargeback_time` TO `idx_account_dispute_time`;