ALTER TABLE `service_accounting_worker`.`task_fee_calculation`
    MODIFY COLUMN `document_id` varchar (255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT ''
    COMMENT 'document id certificate No, e.g: CPF, CNPJ' AFTER `unique_id`;