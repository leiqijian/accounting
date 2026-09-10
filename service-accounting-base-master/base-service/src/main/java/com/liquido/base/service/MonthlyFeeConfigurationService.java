package com.liquido.base.service;

import java.util.List;

import com.liquido.base.pojo.dto.MonthFxLoseConfigDto;
import com.liquido.base.pojo.dto.MonthlyAccountFeeConfigDto;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.vo.BatchQueryProductMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.ListAccountMonthFxLoseVo;
import com.liquido.base.pojo.vo.MonthlyFeeConfigurationVo;
import com.liquido.base.pojo.vo.QueryAccountMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.QueryMonthFeeConfigVo;
import com.liquido.base.pojo.vo.QueryProductMonthlyFeeConfigVo;

public interface MonthlyFeeConfigurationService {

    List<MonthlyFeeConfigurationDto> saveAll(List<MonthlyFeeConfigurationVo> listVo);

    List<MonthlyFeeConfigurationDto> queryProductMonthFeeConfigInfo(
            QueryProductMonthlyFeeConfigVo vo);

    List<MonthlyFeeConfigurationDto> batchQueryProductMonthlyFeeConfigInfo(
            BatchQueryProductMonthlyFeeConfigVo batchVo);

    MonthlyAccountFeeConfigDto queryAccountMonthFeeConfigExclFxLose(
            QueryAccountMonthlyFeeConfigVo vo);

    List<MonthFxLoseConfigDto> listAllAccountMonthFxLose(final ListAccountMonthFxLoseVo vo);

    List<MonthlyFeeConfigurationDto> listMonthlyFeeConfigInfo(final QueryMonthFeeConfigVo vo);

    List<MonthlyFeeConfigurationDto> queryMonthlyFeeConfigurationByIds(final List<Long> ids);
}
