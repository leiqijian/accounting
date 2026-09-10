ALTER TABLE `service_accounting_worker`.`task_fee_calculation` ADD COLUMN `transaction_data_source`
    VARCHAR (20) DEFAULT 'TRADE' NOT NULL COMMENT 'Transaction data source enum, ACCOUNT: transaction data from account service; TRADE: transaction data from trade service' AFTER `final_status_timestamp`;

