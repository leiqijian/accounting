ALTER TABLE service_accounting_worker.task_fee_calculation
    ADD COLUMN `description` varchar(1024) NOT NULL DEFAULT '' COMMENT 'virgo/gemini payer_comment' AFTER comments;