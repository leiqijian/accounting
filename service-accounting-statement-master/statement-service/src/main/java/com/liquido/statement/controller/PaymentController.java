package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.PaymentServiceApi;
import com.liquido.statement.manage.TransactionPayoutManager;
import com.liquido.statement.pojo.dto.PaymentLinkDto;
import com.liquido.statement.pojo.dto.PaymentPayoutDto;
import com.liquido.statement.pojo.dto.payment.PaymentLinkInstallmentsPlanDto;
import com.liquido.statement.pojo.dto.payment.QueryPayoutResultDto;
import com.liquido.statement.pojo.vo.BatchTransactionPayoutVo;
import com.liquido.statement.pojo.vo.CreatePaymentLinkVo;
import com.liquido.statement.pojo.vo.InstallmentsPlanCheckVo;
import com.liquido.statement.pojo.vo.PaymentPayoutVo;
import com.liquido.statement.pojo.vo.QueryInstallmentsPlanVo;
import com.liquido.statement.pojo.vo.QueryPayoutResultVo;
import com.liquido.statement.service.payment.link.PaymentLinkService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentServiceApi {

    private final PaymentLinkService paymentLinkService;
    private final TransactionPayoutManager transactionPayoutManager;

    /**
     * payment payout
     *
     * @param params vo
     */
    @Override
    @PostMapping("/statement/payment/transaction/payout")
    public ResponseDto<PaymentPayoutDto> paymentPayout(
            @RequestBody @Valid final PaymentPayoutVo params) {
        return ResponseDto.success(transactionPayoutManager.executePayout(params));
    }

    /**
     * batch payment payout
     *
     * @param params vo
     */
    @Override
    @PostMapping("/statement/payment/transaction/payout/batch")
    public ResponseDto<List<PaymentPayoutDto>> transactionPayout(
            @RequestBody @Valid final BatchTransactionPayoutVo params) {
        return ResponseDto.success(transactionPayoutManager.executePayout(params));
    }

    /**
     * query payout result
     *
     * @param order vo
     */
    @Override
    @PostMapping("/statement/payment/transaction/payout/result/query")
    public ResponseDto<QueryPayoutResultDto> queryPayoutResult(
            @RequestBody @Valid final QueryPayoutResultVo order) {
        return ResponseDto.success(transactionPayoutManager.queryPayoutResult(order));
    }

    @PostMapping("/statement/payment/payment-link/generate")
    public ResponseDto<PaymentLinkDto> generatePaymentLink(
            @RequestBody @Valid final CreatePaymentLinkVo vo) {
        return ResponseDto.success(paymentLinkService.generatePaymentLink(vo));
    }

    @PostMapping("/statement/payment/installments-plan/list")
    public ResponseDto<List<PaymentLinkInstallmentsPlanDto>> queryInstallmentsPlanList(
            @RequestBody @Valid final QueryInstallmentsPlanVo vo) {
        return ResponseDto.success(paymentLinkService.queryInstallmentsPlan(vo));
    }

    @PostMapping("/statement/payment/installments-plan/check")
    public ResponseDto<Boolean> installmentsPlanCheck(
            @RequestBody @Valid final InstallmentsPlanCheckVo vo) {
        return ResponseDto.success(paymentLinkService.installmentsPlanCheck(vo));
    }
}
