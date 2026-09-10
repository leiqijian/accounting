package com.liquido.base.api;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.AccountFeeConfigurationDto;
import com.liquido.base.pojo.dto.CalculationRuleWeightDto;
import com.liquido.base.pojo.dto.FeeCodeWeightDto;
import com.liquido.base.pojo.vo.BatchAddProductFeeConfigsVo;
import com.liquido.base.pojo.vo.BatchQueryProductFeeConfigInfoVo;
import com.liquido.base.pojo.vo.BatchQueryProductFeeMatchVo;
import com.liquido.base.pojo.vo.QueryProductCalculationRuleVo;
import com.liquido.base.pojo.vo.QueryProductFeeConfigInfoVo;
import com.liquido.base.pojo.vo.QueryProductFeeMatchVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountFeeConfigurationApi {

    @PostMapping("/base/product/fee-configs/batch-add")
    ResponseDto<List<AccountFeeConfigurationDto>> batchAddProductFeeConfigs(
            @Valid @RequestBody BatchAddProductFeeConfigsVo vo);

    @PostMapping("/base/fee-codes/weight/query")
    ResponseDto<FeeCodeWeightDto> queryFeeCodeWeight();

    @PostMapping("/base/calculation-rules/weight/query")
    ResponseDto<CalculationRuleWeightDto> queryCalculationRuleWeight();

    @PostMapping("/base/product/fee-configs/match")
    ResponseDto<List<AccountFeeConfigurationDto>> matchProductFeeConfigs(
            @Valid @RequestBody QueryProductFeeMatchVo vo);

    @PostMapping("/base/product/fee-configs/batch-match")
    ResponseDto<List<AccountFeeConfigurationDto>> batchMatchProductFeeConfigs(
            @Valid @RequestBody BatchQueryProductFeeMatchVo batchVo);

    @PostMapping("/base/product/fee-calculation-rules/query")
    ResponseDto<List<Map<String, String>>> queryProductCalculationRules(
            @Valid @RequestBody QueryProductCalculationRuleVo vo);

    @PostMapping("/base/product/fee-config/query")
    ResponseDto<List<AccountFeeConfigurationDto>> queryProductFeeConfigInfo(
            @Valid @RequestBody QueryProductFeeConfigInfoVo vo);


    @PostMapping("/base/product/fee-config/batch-query")
    ResponseDto<List<AccountFeeConfigurationDto>> batchQueryProductFeeConfigInfo(
            @Valid @RequestBody BatchQueryProductFeeConfigInfoVo batchVo);

}
