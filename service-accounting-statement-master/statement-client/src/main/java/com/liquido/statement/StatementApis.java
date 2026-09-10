package com.liquido.statement;

import com.liquido.statement.api.AccountApi;
import com.liquido.statement.api.AccountConfigApi;
import com.liquido.statement.api.AccountDailyBillApi;
import com.liquido.statement.api.AccountTransferConfigApi;
import com.liquido.statement.api.BatchWithdrawalApi;
import com.liquido.statement.api.DailyExchangeRateApi;
import com.liquido.statement.api.GlobalAccountApi;
import com.liquido.statement.api.HourlyExchangeRateApi;
import com.liquido.statement.api.OldSystemDataMigrationApi;
import com.liquido.statement.api.PaymentServiceApi;
import com.liquido.statement.api.StatementMonitorApi;
import com.liquido.statement.api.SubAccountApi;
import com.liquido.statement.api.SubAccountDailyBillApi;
import com.liquido.statement.api.TransactionBizApi;
import com.liquido.statement.api.TransactionChargeBackOrderApi;
import com.liquido.statement.api.TransactionCostApi;
import com.liquido.statement.api.TransactionCostExtraApi;
import com.liquido.statement.api.TransactionExchangeApi;
import com.liquido.statement.api.TransactionFeeApi;
import com.liquido.statement.api.TransactionInProgressApi;
import com.liquido.statement.api.TransactionMoneyApi;
import com.liquido.statement.api.TransactionSettlementApi;
import com.liquido.statement.api.TransactionSummaryApi;

import org.springframework.cloud.openfeign.FeignClient;

public interface StatementApis extends AccountApi, AccountConfigApi, AccountDailyBillApi,
        AccountTransferConfigApi, DailyExchangeRateApi, GlobalAccountApi, OldSystemDataMigrationApi,
        PaymentServiceApi, StatementMonitorApi, TransactionBizApi, TransactionCostApi,
        TransactionExchangeApi, TransactionFeeApi, TransactionMoneyApi, TransactionSettlementApi,
        TransactionSummaryApi, TransactionChargeBackOrderApi, TransactionCostExtraApi,
        TransactionInProgressApi, HourlyExchangeRateApi, SubAccountApi, SubAccountDailyBillApi,
        BatchWithdrawalApi {

    @FeignClient("${statement.feign.name:service-accounting-statement}")
    interface StatementFeign extends StatementApis {
    }
}
