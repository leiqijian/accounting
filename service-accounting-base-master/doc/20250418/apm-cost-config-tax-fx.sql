
INSERT INTO service_accounting_base.cost_configuration_apm (
active_version, account_id, country_code, transaction_type_code, vendor_code, product_code,
fee_model, fee_name, fee_type, fee_group, fee_on, volume, currency, created_time, updated_time, created_by, updated_by, version, del_flag, remark) VALUES

/* ==============================================   RULES ============================================== */
(20250101, 0,'BR','PAY_IN','','',1,'PIS/COFINS/ISS','TAX','TAX','FEE',0.126800,'USD',now(),now(),0,0,1,0, 'DEFAULT'),
(20250101, 0,'BR','PAY_OUT','','',1,'PIS/COFINS/ISS','TAX','TAX','FEE',0.126800,'USD',now(),now(),0,0,1,0, 'DEFAULT'),

(20250101, 0,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, 'DEFAULT'),
(20250101, 0,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, 'DEFAULT'),

(20250101, 0,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, 'DEFAULT'),
(20250101, 0,'CO','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, 'DEFAULT'),
(20250101, 0,'CO','PAY_OUT','','',1,'GMF','TAX','TAX','AMOUNT',0.0040,'USD',now(),now(),0,0,1,0, 'DEFAULT'),

(20250101, 0,'CL','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, 'DEFAULT'),
(20250101, 0,'CL','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, 'DEFAULT'),

(20250101, 0,'ZA','PAY_IN', '', '', 1, 'VAT', 'TAX', 'TAX', 'FEE', 0.150000, 'USD', NOW(), NOW(), 0, 0, 1, 0, 'DEFAULT'),
(20250101, 0,'ZA','PAY_OUT', '', '', 1, 'VAT', 'TAX', 'TAX', 'FEE', 0.150000, 'USD', NOW(), NOW(), 0, 0, 1, 0, 'DEFAULT'),

(20250101, 0,'PE','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1800,'USD',now(),now(),0,0,1,0, 'DEFAULT'),
(20250101, 0,'PE','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1800,'USD',now(),now(),0,0,1,0, 'DEFAULT'),

/* ==============================================   RULES ============================================== */
/* X6 BR */
(20250101, 83864700725755916,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755916,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755917,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755917,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* DuKpay BR */
(20250101, 83869700725799128,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83869700725799128,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83869700725799129,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83869700725799129,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* DuKpay MX */
(20250101, 83869700725839146,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83869700725839147,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* DuKpay2 BR */
(20250101, 471048525340213543,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213543,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213544,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213544,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* NavigatorX BR */
(20250101, 83864700725755943,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755943,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* NavigatorX MX */
(20250101, 83864700725755944,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755944,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Wiio BR */
(20250101, 83864700725755955,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755955,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939227,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939227,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Wiio_tudovasio BR */
(20250101, 93869700725939351,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939351,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213436,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213436,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* GeeWallet BR */
(20250101, 83864700725755931,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755931,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755932,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755932,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* GeeWallet MX */
(20250101, 83864700725755933,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755933,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755934,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755934,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
/* GeeWallet CO */
(20250101, 93869700725939202,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* GeeWallet CL */
(20250101, 471048525340213423,'CL','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213424,'CL','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* Gee BR */
(20250101, 471048525340213554,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213554,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213555,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213555,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Gee MX */
(20250101, 471048525340213556,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213556,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213557,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213557,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
/* Gee CO */
(20250101, 471048525340213558,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),
/* Gee CL */
(20250101, 471048525340213559,'CL','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213560,'CL','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* Payermax BR*/
(20250101, 83864700725755935,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755935,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213572,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213572,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Payermax MX*/
(20250101, 83864700725755937,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 83864700725755937,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213573,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213573,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Payermax_2 BR*/
(20250101, 471048525340213398,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213398,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Payermax_2 MX*/
(20250101, 471048525340213399,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213399,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Spaceera BR */
(20250101, 93869700725939195,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939195,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939196,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939196,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* PlanckX BR */
(20250101, 93869700725939200,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939200,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939201,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939201,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* PlanckX_V2 BR */
(20250101, 93869700725939271,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939271,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939272,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939272,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Clipspay BR */
(20250101, 93869700725939198,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939198,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Clipspay MX */
(20250101, 93869700725939199,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939199,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
/* Clipspay CO */
(20250101, 471048525340213327,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* Payssion BR */
(20250101, 93869700725939231,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939231,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939232,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939232,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Payssion MX */
(20250101, 93869700725939233,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939233,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939234,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939234,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
/* Payssion CO */
(20250101, 93869700725939287,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939287,'CO','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939235,'CO','PAY_OUT','','',1,'GMF','TAX','TAX','AMOUNT',0.0040,'USD',now(),now(),0,0,1,0, ''),

/* Oriental_Express BR */
(20250101, 93869700725939184,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939184,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Eskimo BR*/
(20250101, 93869700725939241,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939241,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/*  Eskimo MX */
(20250101, 93869700725939242,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939242,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Durablewig BR */
(20250101, 471048525340213361,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),

/* Nebulapart BR */
(20250101, 93869700725939338,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939338,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Foxnovel BR  */
(20250101, 93869700725939361,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939361,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939362,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939362,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Foxnovel MX  */
(20250101, 471048525340213352,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213352,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213353,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213353,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
/* Foxnovel CO  */
(20250101, 471048525340213354,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),
/* Foxnovel CL  */
(20250101, 471048525340213425,'CL','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213426,'CL','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* Foxnovel2 BR  */
(20250101, 471048525340213561,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213561,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213562,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213562,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Foxnovel2 MX  */
(20250101, 471048525340213563,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213563,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213564,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213564,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
/* Foxnovel2 CO  */
(20250101, 471048525340213565,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),
/* Foxnovel2 CL  */
(20250101, 471048525340213567,'CL','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213568,'CL','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* Hung_hing BR */
(20250101, 93869700725939367,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),

/* Migtech BR */
(20250101, 93869700725939313,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939313,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939314,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939314,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Tikipay BR  */
(20250101, 93869700725939368,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939368,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939369,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939369,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Tikipay MX  */
(20250101, 93869700725939370,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939370,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939371,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939371,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Ats BR  */
(20250101, 93869700725939363,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939363,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939364,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939364,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Ats MX  */
(20250101, 93869700725939365,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939365,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939366,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939366,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Talkguest BR */
(20250101, 93869700725939333,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939333,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939334,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939334,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Milian BR  */
(20250101, 471048525340213329,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213329,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213427,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213427,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Milian MX  */
(20250101, 471048525340213330,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213330,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213428,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213428,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Starmoon BR  */
(20250101, 471048525340213323,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213323,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Starmoon MX  */
(20250101, 471048525340213324,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213324,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Qbit BR  */
(20250101, 93869700725939382,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939382,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Qbit MX  */
(20250101, 93869700725939383,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 93869700725939383,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* spaceyy BR  */
(20250101, 471048525340213266,'BR','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213266,'BR','PAY_IN','','',1,'PIS/COFINS/ISS','TAX','TAX','FEE',0.1268,'USD',now(),now(),0,0,1,0, ''),

/* sudutech BR  */
(20250101, 471048525340213355,'BR','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213355,'BR','PAY_IN','','',1,'PIS/COFINS/ISS','TAX','TAX','FEE',0.126800,'USD',now(),now(),0,0,1,0, ''),

/* Oncewell BR*/
(20250101, 471048525340213396,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213396,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Oncewell_shopify BR */
(20250101, 471048525340213397,'BR','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213397,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213397,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Jincase BR*/
(20250101, 471048525340213458,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213458,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Xuchu BR*/
(20250101, 471048525340213442,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213442,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213443,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213443,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Xuchu MX  */
(20250101, 471048525340213444,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213444,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213445,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213445,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Ziyi BR*/
(20250101, 471048525340213477,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213477,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Ziyi MX  */
(20250101, 471048525340213493,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213493,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Tuole BR*/
(20250101, 471048525340213483,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213483,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* Tuole MX  */
(20250101, 471048525340213484,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213484,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Fforder BR*/
(20250101, 471048525340213465,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213465,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* gold_cheer BR */
(20250101, 471048525340213373,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213373,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* stormtime BR*/
(20250101, 471048525340213488,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213488,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
/* stormtime MX  */
(20250101, 471048525340213489,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213489,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Panel */
(20250101, 471048525340213478, 'BR', 'PAY_IN', '', '', 1, 'IOF', 'TAX', 'TAX', 'AMOUNT', 0.003800, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(20250101, 471048525340213478, 'BR', 'PAY_IN', '', '', 1, 'FX', 'FX', 'FX', 'AMOUNT', 0.007500, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(20250101, 471048525340213479, 'MX', 'PAY_IN', '', '', 1, 'VAT', 'TAX', 'TAX', 'FEE', 0.160000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(20250101, 471048525340213479, 'MX', 'PAY_IN', '', '', 1, 'FX', 'FX', 'FX', 'AMOUNT', 0.005000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

/* winwin */
(20250101, 471048525340213598, 'BR', 'PAY_IN', '', '', 1, 'IOF', 'TAX', 'TAX', 'AMOUNT', 0.003800, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(20250101, 471048525340213598, 'BR', 'PAY_IN', '', '', 1, 'FX', 'FX', 'FX', 'AMOUNT', 0.007500, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(20250101, 471048525340213599, 'MX', 'PAY_IN', '', '', 1, 'VAT', 'TAX', 'TAX', 'FEE', 0.160000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),
(20250101, 471048525340213599, 'MX', 'PAY_IN', '', '', 1, 'FX', 'FX', 'FX', 'AMOUNT', 0.005000, 'USD', NOW(), NOW(), 0, 0, 1, 0, ''),

/* Qeeq BR*/
(20250101, 471048525340213535,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213535,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213536,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213536,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* Qeeq MX  */
(20250101, 471048525340213537,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213537,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213538,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213538,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),

/* Quark MX  */
(20250101, 471048525340213539,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213539,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213540,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213540,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
/* Quark CL  */
(20250101, 471048525340213541,'CL','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213542,'CL','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* watchlist_pro BR*/
(20250101, 471048525340213583,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213583,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213584,'BR','PAY_OUT','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213584,'BR','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* redplay_live BR*/
(20250101, 471048525340213608,'BR','PAY_IN','','',1,'IOF','TAX','TAX','AMOUNT',0.003800,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213608,'BR','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.007500,'USD',now(),now(),0,0,1,0, ''),

/* lapay_va MX*/
(20250101, 471048525340213437,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213437,'MX','PAY_IN','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213438,'MX','PAY_OUT','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),
(20250101, 471048525340213438,'MX','PAY_OUT','','',1,'FX','FX','FX','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, ''),


/* ====================================================================================== */
/* 360_slim MX  */
(20250101, 93869700725939213,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 93869700725939213,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* eecom MX  */
(20250101, 471048525340213271,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213271,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* eecom_luvit MX  */
(20250101, 471048525340213332,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213332,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* farmacia_san_jorge MX  */
(20250101, 471048525340213331,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213331,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* hermes_music MX  */
(20250101, 471048525340213315,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213315,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* luis_osvaldo_garcia_cruz MX  */
(20250101, 471048525340213270,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213270,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* sulkin MX  */
(20250101, 93869700725939217,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 93869700725939217,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* magic_box MX  */
(20250101, 471048525340213342,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213342,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* eecom_getfit MX  */
(20250101, 471048525340213359,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213359,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* tejidos_anymax MX  */
(20250101, 471048525340213364,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213364,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* dropkickz MX  */
(20250101, 471048525340213358,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213358,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* eecom_gadgets_and_fun MX  */
(20250101, 471048525340213360,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213360,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* hector_renteria_zavala MX  */
(20250101, 471048525340213272,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213272,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* eecom_luvitshop MX  */
(20250101, 471048525340213409,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213409,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* Blind_creators_shopify MX  */
(20250101, 93869700725939179,'MX','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 93869700725939179,'MX','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1600,'USD',now(),now(),0,0,1,0, ''),

/* ====================================================================================== */
/* crisobela CO  */
(20250101, 93869700725939175,'CO','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 93869700725939175,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* dahl CO  */
(20250101, 93869700725939223,'CO','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 93869700725939223,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* maaji CO  */
(20250101, 93869700725939228,'CO','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 93869700725939228,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* mis_bebes CO  */
(20250101, 471048525340213318,'CO','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213318,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* muy_bonita CO  */
(20250101, 471048525340213269,'CO','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213269,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* we_love_luana CO  */
(20250101, 93869700725939339,'CO','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 93869700725939339,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* bolu2 CO  */
(20250101, 471048525340213383,'CO','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213383,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, ''),

/* givelo CO  */
(20250101, 471048525340213379,'CO','PAY_IN','','',1,'SHOPIFY_FEE','TRANSACTION_FEE','TRANSACTION_FEE','AMOUNT',0.00500,'USD',now(),now(),0,0,1,0, 'SHOPIFY FEE'),
(20250101, 471048525340213379,'CO','PAY_IN','','',1,'VAT','TAX','TAX','FEE',0.1900,'USD',now(),now(),0,0,1,0, '')
;


