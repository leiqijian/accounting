package com.liquido.base.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.base.pojo.dto.BatchPaymentConfigCreateDto;
import com.liquido.base.pojo.dto.PaymentConfigDto;
import com.liquido.base.pojo.vo.BatchCreatePaymentLinkPaymentConfigVo;
import com.liquido.base.pojo.vo.BatchCreatePayoutPaymentConfigVo;
import com.liquido.base.pojo.vo.QueryPaymentConfigVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface PaymentConfigApi {

    @PostMapping("/base/payment-config/payout/create/batch")
    ResponseDto<BatchPaymentConfigCreateDto> batchCreatePayoutPaymentConfig(
            @Valid @RequestBody final BatchCreatePayoutPaymentConfigVo vo);

    @PostMapping("/base/payment-config/payment-link/create/batch")
    ResponseDto<BatchPaymentConfigCreateDto> batchCreatePaymentLinkPaymentConfig(
            @Valid @RequestBody final BatchCreatePaymentLinkPaymentConfigVo vo);

    @PostMapping("/base/payment-config/query")
    ResponseDto<PaymentConfigDto> queryPaymentConfig(
            @Valid @RequestBody final QueryPaymentConfigVo vo);

    @PostMapping("/base/payment-config/{configId}/query")
    ResponseDto<PaymentConfigDto> queryPaymentConfigById(
            @NotNull @PathVariable(name = "configId") final Long configId);
}
