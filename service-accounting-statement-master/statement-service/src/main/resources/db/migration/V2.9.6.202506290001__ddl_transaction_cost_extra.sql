ALTER TABLE `service_accounting_statement`.`transaction_cost_extra`
CHANGE `currency`  `pricing_currency` varchar(3) NOT NULL COMMENT 'pricing currency: cost currency',
CHANGE `fee_usd`  `fee` decimal(26,0) DEFAULT '0' COMMENT 'Transaction fee Amount Unit: cent',
CHANGE `tax_usd`   `tax` decimal(26,0) DEFAULT '0' COMMENT 'Tax Amount Unit: cent',
CHANGE `fx_usd`   `fx` decimal(26,0) DEFAULT '0' COMMENT 'Fx Amount Unit: cent',
CHANGE `extra_fee_usd`  `extra_fee` decimal(26,0) DEFAULT '0' COMMENT 'Extra Transaction Fee Amount Unit: cent',
CHANGE `extra_tax_usd`  `extra_tax` decimal(26,0) DEFAULT '0' COMMENT 'Extra Transaction Tax Amount Unit: cent',
CHANGE `extra_fx_usd`   `extra_fx` decimal(26,0) DEFAULT '0' COMMENT 'Extra Transaction FX Amount Unit: cent',
CHANGE `exchange_fee_usd`          `exchange_fee` decimal(26,0) DEFAULT '0' COMMENT 'Exchange fee',
CHANGE `cdi_profit_income_usd`     `cdi_profit_income` decimal(26,0) DEFAULT '0' COMMENT 'Certificate of Deposit Interbank profit, Unit: cent',
CHANGE `adjustment_fee_usd`        `adjustment_fee` decimal(26,0) DEFAULT '0' COMMENT 'Adjustment fee, Unit: cent';

ALTER TABLE `service_accounting_statement`.`transaction_cost_extra`
ADD COLUMN `settlement_currency` varchar(3) NOT NULL COMMENT ' settlement currency: income currency' AFTER `adjustment_fee`,
ADD COLUMN `exchange_rate` decimal(26,6) NOT NULL COMMENT 'fx rate' AFTER `pricing_currency`;