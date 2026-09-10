package com.liquido.statement.service;

import java.time.LocalDateTime;
import java.util.List;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.entity.HourlyExchangeRate;
import com.liquido.statement.pojo.vo.QueryHourlyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryHourlyLatestExchangeRateVo;
import com.liquido.statement.pojo.vo.RealTimeExchangeRateVo;

public interface HourlyExchangeRateService {

    void batchSaveExchangeRate(final List<RealTimeExchangeRateVo> voList);

    HourlyExchangeRate queryHourlyExchangeRate(final CurrencyEnum sourceCurrency,
                                               final CurrencyEnum targetCurrency,
                                               final LocalDateTime exchangeTime);

    HourlyExchangeRateDto queryHourlyExchangeRate(final QueryHourlyExchangeRateVo vo);

    List<HourlyExchangeRateDto> queryHourlyExchangeRate(final LocalDateTime exchangeTime);

    HourlyExchangeRateDto queryLatestHourlyExchangeRate(QueryHourlyLatestExchangeRateVo vo);
}
