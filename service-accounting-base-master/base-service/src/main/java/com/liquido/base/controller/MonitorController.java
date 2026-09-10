package com.liquido.base.controller;

import javax.validation.Valid;

import com.liquido.base.api.MonitorApi;
import com.liquido.base.pojo.vo.DynamicConstantCreateMonitorVo;
import com.liquido.base.service.MonitorService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
public class MonitorController implements MonitorApi {

    private final MonitorService monitorService;

    @Override
    @PostMapping("/monitor/dynamic/constant/create")
    public ResponseDto<Void> dynamicConstantCreateMonitor(
            @RequestBody @Valid final DynamicConstantCreateMonitorVo vo) {
        monitorService.dynamicConstantCreateMonitor(vo);
        return ResponseDto.success();
    }
}
