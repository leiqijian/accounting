package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.DailyExchangeRateApi;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.RealTimeExchangeRateDto;
import com.liquido.statement.pojo.vo.BatchAddExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateListVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryLatestExchangeRateVo;
import com.liquido.statement.service.DailyExchangeRateService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class DailyExchangeRateController implements DailyExchangeRateApi {
    private final DailyExchangeRateService dailyExchangeRateService;

    @Override
    @PostMapping("/statement/daily/exchange-rate/batch/add")
    public ResponseDto<Void> batchAddDailyExchangeRate(
            @RequestBody @Valid final BatchAddExchangeRateVo vo) {
        dailyExchangeRateService.batchSaveExchangeRate(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/daily/exchange-rate/query")
    public ResponseDto<DailyExchangeRateDto> queryDailyExchangeRate(
            @RequestBody @Valid final QueryDailyExchangeRateVo vo) {
        return ResponseDto.success(dailyExchangeRateService.queryDailyExchangeRate(vo));
    }

    @Override
    @PostMapping("/statement/daily/latest/exchange-rate/query")
    public ResponseDto<RealTimeExchangeRateDto> queryLatestExchangeRate(
            @RequestBody @Valid final QueryLatestExchangeRateVo vo) {
        return ResponseDto.success(dailyExchangeRateService.queryLatestExchangeRate(vo));
    }

    @Override
    @PostMapping("/statement/daily/exchange-rate/list")
    public ResponseDto<List<DailyExchangeRateDto>> queryDailyExchangeRateForList(
            @RequestBody @Valid final QueryDailyExchangeRateListVo vo) {
        return ResponseDto.success(dailyExchangeRateService.queryDailyExchangeRateForList(vo));
    }

}
