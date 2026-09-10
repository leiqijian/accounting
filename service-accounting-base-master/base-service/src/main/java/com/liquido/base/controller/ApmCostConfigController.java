package com.liquido.base.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.Valid;

import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.pojo.dto.ApmCostConfigDto;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.vo.ApmCostConfigVo;
import com.liquido.base.pojo.vo.QueryApmCostConfigVo;
import com.liquido.base.service.CostConfigurationService;
import com.liquido.core.mvc.dto.ResponseDto;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
public class ApmCostConfigController {

    private final CostConfigurationService costConfigurationService;

    // just internal test
    @PostMapping("/base/cost/apm-test/query")
    public ResponseDto<ApmCostConfigurationDto> getApmCostConfig(
            @RequestBody @Valid final ApmCostConfigVo vo) {

        final List<ApmCostConfigDto> configList =
                costConfigurationService.queryApmCostConfig(
                                QueryApmCostConfigVo.builder()
                                        .activeVersion(vo.getActiveVersion()).build())
                        .getCostConfigList();

        if (CollectionUtils.isEmpty(configList)) {
            return ResponseDto.success(ApmCostConfigurationDto.builder()
                    .costConfigList(Collections.emptyList()).build());
        }

        /* Customize(FEE) config */
        List<ApmCostConfigDto> resultList = configList.stream()
                .filter(item -> item.getAccountId().equals(vo.getAccountId())
                        && item.getVendorCode() == vo.getVendor()
                        && item.getProductCode() == vo.getProductCode()
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                .collect(Collectors.toList());

        /* merge apm config */
        resultList.addAll(configList.stream()
                .filter(item -> item.getAccountId().equals(0L)
                        && item.getCountryCode() == vo.getCountry()
                        && item.getTransactionTypeCode() == vo.getTransactionType()
                        && item.getVendorCode() == vo.getVendor()
                        && item.getProductCode() == vo.getProductCode()
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                .collect(Collectors.toList()));

        /* Customize(TAX, FX) config */
        resultList.addAll(configList.stream()
                .filter(item -> vo.getAccountId().equals(item.getAccountId())
                        && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup()))
                .collect(Collectors.toList()));

        /* if no customize(TAX, FX), use default apm config */
        resultList.addAll(configList.stream()
                .filter(item -> item.getAccountId().equals(0L)
                        && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup())
                        && item.getCountryCode() == vo.getCountry()
                        && item.getTransactionTypeCode() == vo.getTransactionType()
                        && (item.getProductCode() == vo.getProductCode()
                        || Objects.isNull(item.getProductCode())))
                .collect(Collectors.toList()));

        // deduplication
        final List<ApmCostConfigDto> dataList = Lists.newArrayList(resultList.stream()
                .collect(Collectors.toMap(ApmCostConfigDto::getId,
                        Function.identity(), (x, y) -> x)).values());
        dataList.sort(Comparator.comparing(ApmCostConfigDto::getId));

        return ResponseDto.success(ApmCostConfigurationDto.builder()
                .costConfigList(dataList).build());
    }
}
