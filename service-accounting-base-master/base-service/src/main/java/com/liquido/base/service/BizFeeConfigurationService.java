package com.liquido.base.service;

import java.util.List;

import com.liquido.base.pojo.dto.BizFeeConfigurationDto;
import com.liquido.base.pojo.vo.QueryBizFeeConfigurationVo;

public interface BizFeeConfigurationService {

    List<BizFeeConfigurationDto> queryBizTransactionFeeConfig(final QueryBizFeeConfigurationVo vo);

}
