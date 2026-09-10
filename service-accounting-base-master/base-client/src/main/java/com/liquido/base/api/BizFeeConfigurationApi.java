package com.liquido.base.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.BizFeeConfigurationDto;
import com.liquido.base.pojo.vo.QueryBizFeeConfigurationVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BizFeeConfigurationApi {

    @PostMapping("/base/biz-fee/configuration/list")
    ResponseDto<List<BizFeeConfigurationDto>> queryBizTransactionFeeConfig(
            @Valid @RequestBody final QueryBizFeeConfigurationVo vo);

}
