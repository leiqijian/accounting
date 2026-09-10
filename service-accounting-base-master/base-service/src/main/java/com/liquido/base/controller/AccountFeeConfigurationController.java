package com.liquido.base.controller;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import com.liquido.base.api.AccountFeeConfigurationApi;
import com.liquido.base.common.properties.CalculationRuleProperties;
import com.liquido.base.common.properties.FeeCodeProperties;
import com.liquido.base.pojo.dto.AccountFeeConfigurationDto;
import com.liquido.base.pojo.dto.CalculationRuleWeightDto;
import com.liquido.base.pojo.dto.FeeCodeWeightDto;
import com.liquido.base.pojo.vo.BatchAddProductFeeConfigsVo;
import com.liquido.base.pojo.vo.BatchQueryProductFeeConfigInfoVo;
import com.liquido.base.pojo.vo.BatchQueryProductFeeMatchVo;
import com.liquido.base.pojo.vo.QueryProductCalculationRuleVo;
import com.liquido.base.pojo.vo.QueryProductFeeConfigInfoVo;
import com.liquido.base.pojo.vo.QueryProductFeeMatchVo;
import com.liquido.base.service.AccountFeeConfigurationService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class AccountFeeConfigurationController implements AccountFeeConfigurationApi {

    private final AccountFeeConfigurationService accountFeeConfigurationService;

    private final FeeCodeProperties feeCodeProperties;

    private final CalculationRuleProperties calculationRuleProperties;

    @Override
    @PostMapping("/base/product/fee-configs/batch-add")
    public ResponseDto<List<AccountFeeConfigurationDto>> batchAddProductFeeConfigs(
            @Valid @RequestBody final BatchAddProductFeeConfigsVo vo) {
        return ResponseDto.success(accountFeeConfigurationService.saveAll(vo.getListVo()));
    }

    @Override
    @PostMapping("/base/fee-codes/weight/query")
    public ResponseDto<FeeCodeWeightDto> queryFeeCodeWeight() {
        return ResponseDto.success(new FeeCodeWeightDto(feeCodeProperties.getWeights()));
    }

    @Override
    @PostMapping("/base/calculation-rules/weight/query")
    public ResponseDto<CalculationRuleWeightDto> queryCalculationRuleWeight() {
        return ResponseDto.success(
                new CalculationRuleWeightDto(calculationRuleProperties.getWeights()));
    }

    @Override
    @PostMapping("/base/product/fee-configs/match")
    public ResponseDto<List<AccountFeeConfigurationDto>> matchProductFeeConfigs(
            @Valid @RequestBody final QueryProductFeeMatchVo vo) {
        return ResponseDto.success(accountFeeConfigurationService.matchProductFeeConfigs(vo));
    }

    @Override
    @PostMapping("/base/product/fee-configs/batch-match")
    public ResponseDto<List<AccountFeeConfigurationDto>> batchMatchProductFeeConfigs(
            @Valid @RequestBody final BatchQueryProductFeeMatchVo batchVo) {
        return ResponseDto.success(
                accountFeeConfigurationService.batchMatchProductFeeConfigs(batchVo));
    }

    @Override
    @PostMapping("/base/product/fee-calculation-rules/query")
    public ResponseDto<List<Map<String, String>>> queryProductCalculationRules(
            final QueryProductCalculationRuleVo vo) {
        return ResponseDto.success(accountFeeConfigurationService.queryProductCalculationRules(vo));
    }

    @Override
    @PostMapping("/base/product/fee-config/query")
    public ResponseDto<List<AccountFeeConfigurationDto>> queryProductFeeConfigInfo(
            @Valid @RequestBody QueryProductFeeConfigInfoVo vo) {
        return ResponseDto.success(accountFeeConfigurationService.queryProductFeeConfigInfo(vo));
    }

    @Override
    @PostMapping("/base/product/fee-config/batch-query")
    public ResponseDto<List<AccountFeeConfigurationDto>> batchQueryProductFeeConfigInfo(
            @Valid @RequestBody BatchQueryProductFeeConfigInfoVo batchVo) {
        return ResponseDto.success(
                accountFeeConfigurationService.batchQueryProductFeeConfigInfo(batchVo));
    }

}
