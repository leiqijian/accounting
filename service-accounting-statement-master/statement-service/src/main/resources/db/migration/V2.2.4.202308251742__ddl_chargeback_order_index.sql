ALTER TABLE `service_accounting_statement`.`transaction_charge_back_order`
    ADD INDEX `idx_status_defense_deadline`(`status`, `defense_deadline`) USING BTREE;