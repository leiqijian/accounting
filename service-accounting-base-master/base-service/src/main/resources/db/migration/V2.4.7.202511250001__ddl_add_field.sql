ALTER TABLE service_accounting_base.cost_configuration_apm
    ADD COLUMN min_volume decimal(26, 0) NOT NULL DEFAULT '0' COMMENT 'min volume' AFTER volume,
    ADD COLUMN max_volume decimal(26, 0) NOT NULL DEFAULT '0' COMMENT 'max volume' AFTER min_volume;

