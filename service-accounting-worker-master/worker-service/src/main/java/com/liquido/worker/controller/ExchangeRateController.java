package com.liquido.worker.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.worker.pojo.vo.InitExchangeManualVo;
import com.liquido.worker.service.calculate.ExchangeRateManager;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateManager manager;

    @PostMapping("/worker/exchange-rate/init/manual")
    public ResponseDto<String> initExchangeManual(
            @RequestBody @Valid final InitExchangeManualVo vo) {
        return ResponseDto.success(manager.initExchangeRateManual(vo));
    }
}
