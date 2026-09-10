package com.liquido.base.controller;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.base.api.PaymentConfigApi;
import com.liquido.base.pojo.dto.BatchPaymentConfigCreateDto;
import com.liquido.base.pojo.dto.PaymentConfigDto;
import com.liquido.base.pojo.vo.BatchCreatePaymentLinkPaymentConfigVo;
import com.liquido.base.pojo.vo.BatchCreatePayoutPaymentConfigVo;
import com.liquido.base.pojo.vo.QueryPaymentConfigVo;
import com.liquido.base.service.PaymentConfigService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class PaymentConfigController implements PaymentConfigApi {

    private final PaymentConfigService paymentConfigService;

    @Override
    @PostMapping("/base/payment-config/payout/create/batch")
    public ResponseDto<BatchPaymentConfigCreateDto> batchCreatePayoutPaymentConfig(
            @Valid @RequestBody final BatchCreatePayoutPaymentConfigVo vo) {
        return ResponseDto.success(paymentConfigService.batchCreatePayoutPaymentConfig(vo));
    }

    @Override
    @PostMapping("/base/payment-config/payment-link/create/batch")
    public ResponseDto<BatchPaymentConfigCreateDto> batchCreatePaymentLinkPaymentConfig(
            @Valid @RequestBody final BatchCreatePaymentLinkPaymentConfigVo vo) {
        return ResponseDto.success(paymentConfigService.batchCreatePaymentLinkPaymentConfig(vo));
    }

    /**
     * Add CountryProduct
     *
     * @param vo
     * @return database insert id
     */
    @Override
    @PostMapping("/base/payment-config/query")
    public ResponseDto<PaymentConfigDto> queryPaymentConfig(
            @Valid @RequestBody final QueryPaymentConfigVo vo) {
        return ResponseDto.success(paymentConfigService.queryPaymentConfig(vo));
    }

    /**
     * Add CountryProduct
     *
     * @param configId
     * @return database insert id
     */
    @Override
    @PostMapping("/base/payment-config/{configId}/query")
    public ResponseDto<PaymentConfigDto> queryPaymentConfigById(
            @NotNull @PathVariable(name = "configId") final Long configId) {
        return ResponseDto.success(paymentConfigService.queryPaymentConfig(configId));
    }

}
