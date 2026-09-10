ALTER TABLE service_accounting_worker.payment_link
    ADD COLUMN `sub_merchant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'sub merchant id' AFTER `appendix`;