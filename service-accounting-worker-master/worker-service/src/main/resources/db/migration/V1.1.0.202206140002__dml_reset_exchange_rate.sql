UPDATE service_accounting_worker.daily_exchange_rate
SET exchange_rate = ROUND(exchange_rate, 4),
    merchant_rate = ROUND(merchant_rate, 4);