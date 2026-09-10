package com.liquido.worker.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.AccountProductVersionDto;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.worker.pojo.vo.QueryMerchantMonthVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Account product monthly fee configuration generation
 */
public interface MonthFeeConfigGenerationApi {

    @PostMapping("/worker/product-version/init")
    ResponseDto<List<AccountProductVersionDto>> initProductVersion();

    @PostMapping("/worker/merchant/month-fee-config/generate")
    ResponseDto<String> generateMerchantMonthFeeConfig(
            @RequestBody @Valid final QueryMerchantMonthVo vo);


}
