package com.liquido.base.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.api.WorkingDayApi;
import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.base.pojo.vo.InitWorkingDayVo;
import com.liquido.base.pojo.vo.QueryWorkingDayVo;
import com.liquido.base.service.WorkingDayService;
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
public class WorkingDayController implements WorkingDayApi {

    private final WorkingDayService workingDayService;

    @Override
    @PostMapping("/base/working-day/init")
    public ResponseDto<List<WorkingDayDto>> initWorkingDay(
            @RequestBody @Valid final InitWorkingDayVo vo) {

        workingDayService.initWorkingDay(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/base/working-day/query")
    public ResponseDto<List<WorkingDayDto>> queryWorkingDay(
            @RequestBody @Valid final QueryWorkingDayVo vo) {

        return ResponseDto.success(workingDayService.queryWorkingDay(vo));
    }
}
