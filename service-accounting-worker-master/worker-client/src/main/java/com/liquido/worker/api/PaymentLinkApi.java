package com.liquido.worker.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PagePaymentLinkDto;
import com.liquido.worker.pojo.dto.QueryPaymentLinkDto;
import com.liquido.worker.pojo.vo.BatchQueryPaymentLinkVo;
import com.liquido.worker.pojo.vo.PagePaymentLinkVo;
import com.liquido.worker.pojo.vo.QueryPaymentLinkVo;
import com.liquido.worker.pojo.vo.SyncDataVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface PaymentLinkApi {

    @PostMapping("/worker/payment-link/sync")
    ResponseDto<Void> syncPaymentLink(@RequestBody @Valid final SyncDataVo vo);

    @PostMapping("/worker/payment-link/page")
    ResponseDto<PageVo<PagePaymentLinkDto>> pagePaymentLink(
            @RequestBody @Valid final PagePaymentLinkVo vo);

    @PostMapping("/worker/payment-link/query")
    ResponseDto<QueryPaymentLinkDto> queryPaymentLink(
            @RequestBody @Valid final QueryPaymentLinkVo vo);

    @PostMapping("/worker/payment-link/existed/orders/batch/query")
    ResponseDto<List<String>> queryExistsPaymentLinkOrderIds(
            @RequestBody @Valid final BatchQueryPaymentLinkVo vo);

}
