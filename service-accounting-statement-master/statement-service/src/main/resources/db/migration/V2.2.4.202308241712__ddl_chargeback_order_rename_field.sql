ALTER TABLE `service_accounting_statement`.`transaction_charge_back_order`
    CHANGE COLUMN `defense_appendix` `defense_appendix_ids` json NULL COMMENT 'defense appendix' AFTER `defense_description`;