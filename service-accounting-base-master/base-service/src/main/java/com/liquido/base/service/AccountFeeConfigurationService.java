package com.liquido.base.service;

import java.util.List;
import java.util.Map;

import com.liquido.base.pojo.dto.AccountFeeConfigurationDto;
import com.liquido.base.pojo.vo.AccountFeeConfigurationVo;
import com.liquido.base.pojo.vo.BatchQueryProductFeeConfigInfoVo;
import com.liquido.base.pojo.vo.BatchQueryProductFeeMatchVo;
import com.liquido.base.pojo.vo.QueryProductCalculationRuleVo;
import com.liquido.base.pojo.vo.QueryProductFeeConfigInfoVo;
import com.liquido.base.pojo.vo.QueryProductFeeMatchVo;

public interface AccountFeeConfigurationService {

    List<AccountFeeConfigurationDto> saveAll(List<AccountFeeConfigurationVo> listVo);

    List<AccountFeeConfigurationDto> matchProductFeeConfigs(QueryProductFeeMatchVo vo);

    List<AccountFeeConfigurationDto> batchMatchProductFeeConfigs(
            BatchQueryProductFeeMatchVo batchVo);

    List<Map<String, String>> queryProductCalculationRules(QueryProductCalculationRuleVo vo);

    List<AccountFeeConfigurationDto> queryProductFeeConfigInfo(
            QueryProductFeeConfigInfoVo vo);

    List<AccountFeeConfigurationDto> batchQueryProductFeeConfigInfo(
            BatchQueryProductFeeConfigInfoVo batchVo);

}
