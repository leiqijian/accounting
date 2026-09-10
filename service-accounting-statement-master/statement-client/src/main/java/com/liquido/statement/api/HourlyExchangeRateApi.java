package com.liquido.statement.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.vo.ListHourlyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryHourlyLatestExchangeRateVo;
import com.liquido.statement.pojo.vo.RealTimeExchangeRateVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface HourlyExchangeRateApi {

    @PostMapping("/statement/hourly/exchange-rate/query")
    ResponseDto<List<HourlyExchangeRateDto>> listHourlyExchangeRate(
            @RequestBody @Valid final ListHourlyExchangeRateVo vo);

    @PostMapping("/statement/hourly/exchange-rate/save")
    ResponseDto<Void> saveHourlyExchangeRate(
            @RequestBody @Valid final List<RealTimeExchangeRateVo> voList);

    @PostMapping("/statement/hourly/latest/exchange-rate/query")
    ResponseDto<HourlyExchangeRateDto> queryLatestHourlyExchangeRate(
            @RequestBody @Valid final QueryHourlyLatestExchangeRateVo vo);
}
