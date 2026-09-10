package com.liquido.base.api;

import javax.validation.Valid;

import com.liquido.base.pojo.vo.DynamicConstantCreateMonitorVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MonitorApi {

    @PostMapping("/monitor/dynamic/constant/create")
    ResponseDto<Void> dynamicConstantCreateMonitor(
            @RequestBody @Valid final DynamicConstantCreateMonitorVo vo);
}
