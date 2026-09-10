package com.liquido.statement.service;

import java.util.List;

import com.liquido.statement.pojo.bo.FxRateInitKey;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.RealTimeExchangeRateDto;
import com.liquido.statement.pojo.vo.BatchAddExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateListVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryLatestExchangeRateVo;

public interface DailyExchangeRateService {

    void batchSaveExchangeRate(final BatchAddExchangeRateVo listVo);

    RealTimeExchangeRateDto queryLatestExchangeRate(final QueryLatestExchangeRateVo vo);

    DailyExchangeRateDto queryDailyExchangeRate(final QueryDailyExchangeRateVo vo);

    List<DailyExchangeRateDto> queryDailyExchangeRateForList(final QueryDailyExchangeRateListVo vo);

    List<DailyExchangeRateDto> queryDailyExchangeRateFromCache(final QueryDailyExchangeRateListVo vo);

    DailyExchangeRateDto queryDailyExchangeRate(final FxRateInitKey key);

    DailyExchangeRateDto loadDailyExchangeRate(final FxRateInitKey vo);
}
