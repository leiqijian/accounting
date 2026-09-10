package com.liquido.statement.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.TransactionMoneyDto;
import com.liquido.statement.pojo.vo.CancelAccountHoldingVo;
import com.liquido.statement.pojo.vo.CancelHoldingDocumentVo;
import com.liquido.statement.pojo.vo.CancelHoldingTransactionVo;
import com.liquido.statement.pojo.vo.FillFieldTransactionMoneyVo;
import com.liquido.statement.pojo.vo.ListTransactionMoneyVo;

import lombok.NonNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionMoneyApi {

    @PostMapping("/statement/transaction-money/info")
    ResponseDto<List<TransactionMoneyDto>> listTransactionMoneyInfo(
            @RequestBody @Valid final ListTransactionMoneyVo vo);

    @PostMapping("/statement/transaction/pending-amount/query/{accountId}")
    ResponseDto<BigDecimal> queryPendingAmount(
            @PathVariable(name = "accountId") @NonNull final Long accountId);

    @PostMapping("/statement/transaction/holding-amount/query/{accountId}")
    ResponseDto<BigDecimal> queryHoldingAmount(
            @PathVariable(name = "accountId") @NonNull final Long accountId);

    /**
     * cancel holding amount by documentIds
     *
     * @param order order
     */
    @PostMapping("/statement/transaction/holding-document/cancel")
    ResponseDto<Set<Long>> cancelHoldingByDocumentIds(
            @RequestBody @Valid final CancelHoldingDocumentVo order);

    /**
     * cancel holding amount by transactionIds
     *
     * @param order
     * @return
     */
    @PostMapping("/statement/transaction/holding-transaction/cancel")
    ResponseDto<Set<Long>> cancelHoldingByTransactionIds(
            @RequestBody @Valid final CancelHoldingTransactionVo order);

    /**
     * cancel all holding order
     *
     * @param order
     * @return
     */
    @PostMapping("/statement/transaction/holding-order/all/cancel")
    ResponseDto<Set<Long>> cancelAllHoldingOrder(
            @RequestBody @Valid final CancelAccountHoldingVo order);

    @PostMapping("/statement/transaction/field/fill")
    ResponseDto<Void> fillTransactionMoneyField(
            @RequestBody @Valid final List<FillFieldTransactionMoneyVo> list);
}
