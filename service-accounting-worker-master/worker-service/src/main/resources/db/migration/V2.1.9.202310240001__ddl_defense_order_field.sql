ALTER TABLE `service_accounting_worker`.`defense_order`
    MODIFY COLUMN `original_id` varchar (128) NOT NULL COMMENT 'original id' AFTER `id`;