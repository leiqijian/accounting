ALTER TABLE service_accounting_worker.payment_link
    ADD COLUMN `appendix` json DEFAULT NULL COMMENT 'json to record all of the appendix' AFTER `user_phone`;

ALTER TABLE service_accounting_worker.payment_link DROP COLUMN `payment_document_url`;

ALTER TABLE service_accounting_worker.payment_link DROP COLUMN `supplement_document_url`;