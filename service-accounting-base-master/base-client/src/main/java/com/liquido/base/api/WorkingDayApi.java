package com.liquido.base.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.base.pojo.vo.InitWorkingDayVo;
import com.liquido.base.pojo.vo.QueryWorkingDayVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface WorkingDayApi {

    @PostMapping("/base/working-day/init")
    ResponseDto<List<WorkingDayDto>> initWorkingDay(
            @RequestBody @Valid final InitWorkingDayVo vo);

    @PostMapping("/base/working-day/query")
    ResponseDto<List<WorkingDayDto>> queryWorkingDay(
            @RequestBody @Valid final QueryWorkingDayVo vo);
}
