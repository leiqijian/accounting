package com.liquido.statement.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.RealTimeExchangeRateDto;
import com.liquido.statement.pojo.vo.BatchAddExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateListVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryLatestExchangeRateVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface DailyExchangeRateApi {

    @PostMapping("/statement/daily/exchange-rate/batch/add")
    ResponseDto<Void> batchAddDailyExchangeRate(
            @RequestBody @Valid final BatchAddExchangeRateVo vo);

    @PostMapping("/statement/daily/exchange-rate/query")
    ResponseDto<DailyExchangeRateDto> queryDailyExchangeRate(
            @RequestBody @Valid final QueryDailyExchangeRateVo vo);

    @PostMapping("/statement/daily/latest/exchange-rate/query")
    ResponseDto<RealTimeExchangeRateDto> queryLatestExchangeRate(
            @RequestBody @Valid final QueryLatestExchangeRateVo vo);

    @PostMapping("/statement/daily/exchange-rate/list")
    ResponseDto<List<DailyExchangeRateDto>> queryDailyExchangeRateForList(
            @RequestBody @Valid final QueryDailyExchangeRateListVo vo);

}
