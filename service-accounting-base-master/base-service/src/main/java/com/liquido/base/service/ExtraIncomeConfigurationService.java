package com.liquido.base.service;

import java.util.List;

import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.vo.QueryExtraIncomeConfigurationVo;

public interface ExtraIncomeConfigurationService {

    List<ExtraIncomeConfigurationDto> queryExtraIncomeConfiguration(
            final QueryExtraIncomeConfigurationVo vo);
}
