ALTER TABLE service_accounting_worker.payment_link
    ADD COLUMN `description` varchar(512) NOT NULL DEFAULT '' COMMENT 'cashier.payment_link description' AFTER `appendix`;