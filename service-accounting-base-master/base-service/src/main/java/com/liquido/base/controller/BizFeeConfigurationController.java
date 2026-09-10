package com.liquido.base.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.api.BizFeeConfigurationApi;
import com.liquido.base.pojo.dto.BizFeeConfigurationDto;
import com.liquido.base.pojo.vo.QueryBizFeeConfigurationVo;
import com.liquido.base.service.BizFeeConfigurationService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BizFeeConfigurationController implements BizFeeConfigurationApi {
    private final BizFeeConfigurationService bizFeeConfigurationService;

    /**
     * query biz transaction fee config
     *
     * @param vo
     * @return
     */
    @Override
    @PostMapping("/base/biz-fee/configuration/list")
    public ResponseDto<List<BizFeeConfigurationDto>> queryBizTransactionFeeConfig(
            @Valid @RequestBody final QueryBizFeeConfigurationVo vo) {

        return ResponseDto.success(bizFeeConfigurationService.queryBizTransactionFeeConfig(vo));
    }

}
