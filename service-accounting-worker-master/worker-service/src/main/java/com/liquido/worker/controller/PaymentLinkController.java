package com.liquido.worker.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.api.PaymentLinkApi;
import com.liquido.worker.pojo.dto.PagePaymentLinkDto;
import com.liquido.worker.pojo.dto.QueryPaymentLinkDto;
import com.liquido.worker.pojo.vo.BatchQueryPaymentLinkVo;
import com.liquido.worker.pojo.vo.PagePaymentLinkVo;
import com.liquido.worker.pojo.vo.QueryPaymentLinkVo;
import com.liquido.worker.pojo.vo.SyncDataVo;
import com.liquido.worker.service.PaymentLinkService;
import com.liquido.worker.service.sync.PaymentLinkSyncService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class PaymentLinkController implements PaymentLinkApi {

    private final PaymentLinkSyncService paymentLinkSyncService;
    private final PaymentLinkService paymentLinkService;

    @Override
    @PostMapping("/worker/payment-link/sync")
    public ResponseDto<Void> syncPaymentLink(@RequestBody @Valid final SyncDataVo vo) {
        paymentLinkSyncService.sync(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/worker/payment-link/page")
    public ResponseDto<PageVo<PagePaymentLinkDto>> pagePaymentLink(
            @RequestBody @Valid final PagePaymentLinkVo vo) {
        return ResponseDto.success(paymentLinkService.pagePaymentLink(vo));
    }

    @Override
    @PostMapping("/worker/payment-link/query")
    public ResponseDto<QueryPaymentLinkDto> queryPaymentLink(
            @RequestBody @Valid final QueryPaymentLinkVo vo) {
        return ResponseDto.success(paymentLinkService.queryPaymentLink(vo));
    }


    @Override
    @PostMapping("/worker/payment-link/existed/orders/batch/query")
    public ResponseDto<List<String>> queryExistsPaymentLinkOrderIds(
            @RequestBody @Valid final BatchQueryPaymentLinkVo vo) {
        return ResponseDto.success(paymentLinkService.queryExistsPaymentLinkOrderIds(vo));
    }

}
