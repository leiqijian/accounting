package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.HourlyExchangeRateApi;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.vo.ListHourlyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryHourlyLatestExchangeRateVo;
import com.liquido.statement.pojo.vo.RealTimeExchangeRateVo;
import com.liquido.statement.service.HourlyExchangeRateService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class HourlyExchangeRateController implements HourlyExchangeRateApi {

    private final HourlyExchangeRateService hourlyExchangeRateService;

    @Override
    @PostMapping("/statement/hourly/exchange-rate/query")
    public ResponseDto<List<HourlyExchangeRateDto>> listHourlyExchangeRate(
            @RequestBody @Valid final ListHourlyExchangeRateVo vo) {
        return ResponseDto.success(
                hourlyExchangeRateService.queryHourlyExchangeRate(vo.getExchangeTime()));
    }

    @PostMapping("/statement/hourly/exchange-rate/save")
    public ResponseDto<Void> saveHourlyExchangeRate(
            @RequestBody @Valid final List<RealTimeExchangeRateVo> voList) {
        hourlyExchangeRateService.batchSaveExchangeRate(voList);
        return ResponseDto.success();
    }


    @PostMapping("/statement/hourly/latest/exchange-rate/query")
    public ResponseDto<HourlyExchangeRateDto> queryLatestHourlyExchangeRate(
            @RequestBody @Valid final QueryHourlyLatestExchangeRateVo vo) {
        return ResponseDto.success(hourlyExchangeRateService.queryLatestHourlyExchangeRate(vo));
    }
}
