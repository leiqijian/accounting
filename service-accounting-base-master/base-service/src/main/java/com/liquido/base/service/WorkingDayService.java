package com.liquido.base.service;


import java.util.List;

import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.base.pojo.vo.InitWorkingDayVo;
import com.liquido.base.pojo.vo.QueryWorkingDayVo;

public interface WorkingDayService {

    /**
     * initWorkingDay
     *
     * @param vo
     */
    void initWorkingDay(final InitWorkingDayVo vo);

    List<WorkingDayDto> queryWorkingDay(final QueryWorkingDayVo vo);

}
