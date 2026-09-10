ALTER TABLE `service_accounting_base`.`account_fee_configuration` ADD COLUMN `fee_code` varchar(32) NOT NULL
    COMMENT 'FeeCodeEnum：TRANSACTION_FEE/INSTALLMENT_FEE/ANTICIPATION_FEE/REFUND_FEE/CHARGE_BACK_FEE/FX/TAX' AFTER `fee_name`;


ALTER TABLE `service_accounting_base`.`monthly_fee_configuration` ADD COLUMN `fee_code` varchar(32) NOT NULL
    COMMENT 'FeeCodeEnum：TRANSACTION_FEE/INSTALLMENT_FEE/ANTICIPATION_FEE/REFUND_FEE/CHARGE_BACK_FEE/FX/TAX' AFTER `fee_name`;