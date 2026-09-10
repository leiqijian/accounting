package com.liquido.worker.controller;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.AccountProductVersionDto;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.worker.api.MonthFeeConfigGenerationApi;
import com.liquido.worker.pojo.vo.CheckMonthFeeConfigVo;
import com.liquido.worker.pojo.vo.QueryMerchantMonthVo;
import com.liquido.worker.service.fee.MonthFeeConfigGenerationService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class MonthFeeConfigGenerationController implements MonthFeeConfigGenerationApi {

    private final MonthFeeConfigGenerationService monthFeeConfigGenerationService;

    @Override
    @PostMapping("/worker/product-version/init")
    public ResponseDto<List<AccountProductVersionDto>> initProductVersion() {
        List<AccountProductVersionDto> productVersionList =
                monthFeeConfigGenerationService.initAllProductVersion();
        return ResponseDto.success(productVersionList);
    }

    @PostMapping("/worker/merchant/month-fee-config/check")
    public ResponseDto<Map<String, Boolean>> checkMonthFeeConfig(
            @RequestBody @Valid final CheckMonthFeeConfigVo vo) {
        return ResponseDto.success(Map.of("hasError",
                monthFeeConfigGenerationService.checkMonthFeeConfig(
                        vo.getTimezone(), vo.getActiveDate())));
    }

    @Override
    @PostMapping("/worker/merchant/month-fee-config/generate")
    public ResponseDto<String> generateMerchantMonthFeeConfig(
            @RequestBody @Valid final QueryMerchantMonthVo vo) {
        final Integer productSize =
                monthFeeConfigGenerationService.generateMerchantMonthFeeConfig(vo);

        // Generation started for "productSize" products
        return ResponseDto.success("Generation started for " + productSize + " products.");
    }

}
