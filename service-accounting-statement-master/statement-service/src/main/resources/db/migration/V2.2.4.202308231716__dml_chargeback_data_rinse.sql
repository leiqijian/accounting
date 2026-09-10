UPDATE `service_accounting_statement`.`transaction_charge_back_order`
SET status = 'CHARGE_BACK' WHERE status = 'PENDING';

UPDATE `service_accounting_statement`.`transaction_charge_back_order`
SET status = 'UNDER_DEFENSE' WHERE status = 'PROCESSING';

UPDATE `service_accounting_statement`.`transaction_charge_back_order`
SET status = 'DEFENSE_WON' WHERE status = 'SUCCESSFUL';

UPDATE `service_accounting_statement`.`transaction_charge_back_order`
SET status = 'DEFENSE_LOST' WHERE status = 'FAILED';

UPDATE `service_accounting_statement`.`transaction_charge_back_order`
SET days_left_to_defend = 90, defense_deadline = DATE_ADD(chargeback_time, INTERVAL 90 DAY);
