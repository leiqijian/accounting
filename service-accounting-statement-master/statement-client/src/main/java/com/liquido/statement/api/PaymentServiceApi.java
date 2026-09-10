package com.liquido.statement.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.PaymentPayoutDto;
import com.liquido.statement.pojo.dto.payment.QueryPayoutResultDto;
import com.liquido.statement.pojo.vo.BatchTransactionPayoutVo;
import com.liquido.statement.pojo.vo.PaymentPayoutVo;
import com.liquido.statement.pojo.vo.QueryPayoutResultVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface PaymentServiceApi {

    /**
     * payment payout
     *
     * @param order vo
     */
    @PostMapping("/statement/payment/transaction/payout")
    ResponseDto<PaymentPayoutDto> paymentPayout(@RequestBody @Valid final PaymentPayoutVo order);

    /**
     * batch payment payout
     *
     * @param order vo
     */
    @PostMapping("/statement/payment/transaction/payout/batch")
    ResponseDto<List<PaymentPayoutDto>> transactionPayout(
            @RequestBody @Valid final BatchTransactionPayoutVo order);

    /**
     * @param order vo
     */
    @PostMapping("/statement/payment/transaction/payout/result/query")
    ResponseDto<QueryPayoutResultDto> queryPayoutResult(
            @RequestBody @Valid final QueryPayoutResultVo order);

}
