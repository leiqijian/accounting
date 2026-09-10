
ALTER TABLE service_accounting_base.cost_configuration_universal RENAME TO cost_configuration_apm;

ALTER TABLE service_accounting_base.cost_configuration_version
    MODIFY COLUMN `cost_type` varchar(32) NOT NULL DEFAULT 'APM' COMMENT 'APM, CARD' AFTER `id`;

TRUNCATE TABLE service_accounting_base.cost_configuration_version;

INSERT INTO service_accounting_base.cost_configuration_version
set cost_type='APM', created_time=now(), updated_time=now(), version=20250101, del_flag=0,remark='';

INSERT INTO service_accounting_base.cost_configuration_version
set cost_type='CARD', created_time=now(), updated_time=now(), version=20250101, del_flag=0,remark='';

INSERT INTO service_accounting_base.cost_configuration_version
set cost_type='EXTRA_INCOME', created_time=now(), updated_time=now(), version=20250101, del_flag=0,remark='';
