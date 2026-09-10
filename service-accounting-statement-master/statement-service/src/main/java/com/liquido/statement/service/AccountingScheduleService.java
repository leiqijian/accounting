package com.liquido.statement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.liquido.base.enums.AccountingScheduleStateEnum;
import com.liquido.statement.pojo.dto.AccountingCalendarDto;
import com.liquido.statement.pojo.entity.AccountingSchedule;
import com.liquido.statement.pojo.vo.QueryAccountingCalendarVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;

public interface AccountingScheduleService {

    List<AccountingSchedule> batchSave(final List<TransactionMoneyVo> orderList);

    BigDecimal queryPendingAccountingAmount(final Long accountId, final LocalDate accountingDate);

    BigDecimal queryAccountingAmount(final Long accountId, final AccountingScheduleStateEnum state);

    void updateAccounted(final Long accountId, final LocalDate accountingDate);

    List<AccountingCalendarDto> queryAccountingCalendarList(final QueryAccountingCalendarVo vo);
}
