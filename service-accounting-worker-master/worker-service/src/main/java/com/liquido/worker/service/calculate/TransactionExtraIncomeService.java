package com.liquido.worker.service.calculate;

import java.util.List;

import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.worker.pojo.bo.TransactionExtraIncomeBo;
import com.liquido.worker.pojo.bo.TransactionMoneyBo;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;

public interface TransactionExtraIncomeService {

    List<TransactionExtraIncomeBo> calculateExtraIncome(
            final TransactionMoneyBo transactionMoneyBo,
            final DailyExchangeRateDto dailyExchangeRateDto,
            final List<ExtraIncomeConfigurationDto> extraFeeConfig);

    List<ExtraIncomeConfigurationDto> queryExtraIncomeConfig(
            final AccountDto accountDto,
            final TaskFeeCalculation taskFee);
}
