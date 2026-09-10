alter table transaction_ratio
    modify success_rate decimal(10, 4) default 0.0000 not null;