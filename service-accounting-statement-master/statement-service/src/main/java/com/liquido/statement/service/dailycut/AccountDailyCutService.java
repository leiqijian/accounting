package com.liquido.statement.service.dailycut;

import com.liquido.statement.pojo.bo.DailyBillSummaryBo;
import com.liquido.statement.pojo.entity.AccountDailyBill;

public interface AccountDailyCutService {

    AccountDailyBill executeAccountDailyCut(final DailyBillSummaryBo summary);
}
