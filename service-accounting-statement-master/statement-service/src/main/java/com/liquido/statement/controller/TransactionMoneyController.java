package com.liquido.statement.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.TransactionMoneyApi;
import com.liquido.statement.manage.HoldingTransactionManager;
import com.liquido.statement.pojo.dto.TransactionMoneyDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.vo.CancelAccountHoldingVo;
import com.liquido.statement.pojo.vo.CancelHoldingDocumentVo;
import com.liquido.statement.pojo.vo.CancelHoldingTransactionVo;
import com.liquido.statement.pojo.vo.FillFieldTransactionMoneyVo;
import com.liquido.statement.pojo.vo.ListTransactionMoneyVo;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.TransactionMoneyService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class TransactionMoneyController implements TransactionMoneyApi {

    private final AccountService accountService;
    private final TransactionMoneyService transactionMoneyService;
    private final HoldingTransactionManager holdingTransactionManager;

    @Override
    @PostMapping("/statement/transaction-money/info")
    public ResponseDto<List<TransactionMoneyDto>> listTransactionMoneyInfo(
            @RequestBody @Valid final ListTransactionMoneyVo vo) {
        return ResponseDto.success(transactionMoneyService.listTransactionMoneyInfo(vo));
    }

    @Override
    @PostMapping("/statement/transaction/pending-amount/query/{accountId}")
    public ResponseDto<BigDecimal> queryPendingAmount(
            @PathVariable(name = "accountId") @NonNull final Long accountId) {

        final Account account = accountService.getById(accountId);
        return ResponseDto.success(transactionMoneyService.getPendingBalance(account));
    }

    @Override
    @PostMapping("/statement/transaction/holding-amount/query/{accountId}")
    public ResponseDto<BigDecimal> queryHoldingAmount(
            @PathVariable(name = "accountId") @NonNull final Long accountId) {

        return ResponseDto.success(transactionMoneyService.getHoldingBalance(accountId));
    }

    /**
     * Cancel holding amount by documentIds
     *
     * @param order order
     */
    @Override
    @PostMapping("/statement/transaction/holding-document/cancel")
    public ResponseDto<Set<Long>> cancelHoldingByDocumentIds(
            @RequestBody @Valid final CancelHoldingDocumentVo order) {
        return ResponseDto.success(holdingTransactionManager.cancelHoldingByDocumentIds(order));
    }

    /**
     * Cancel holding amount by transactionIds
     *
     * @param order
     * @return
     */
    @Override
    @PostMapping("/statement/transaction/holding-transaction/cancel")
    public ResponseDto<Set<Long>> cancelHoldingByTransactionIds(
            @RequestBody @Valid final CancelHoldingTransactionVo order) {
        return ResponseDto.success(holdingTransactionManager.cancelHoldingByTransactionIds(order));
    }

    @Override
    @PostMapping("/statement/transaction/holding-order/all/cancel")
    public ResponseDto<Set<Long>> cancelAllHoldingOrder(
            @RequestBody @Valid final CancelAccountHoldingVo order) {
        return ResponseDto.success(holdingTransactionManager.cancelAllHoldingOrder(order));
    }

    /**
     * fill documentId and field payerCity and targetName into other
     */
    @Override
    @PostMapping("/statement/transaction/field/fill")
    public ResponseDto<Void> fillTransactionMoneyField(
            @RequestBody @Valid final List<FillFieldTransactionMoneyVo> list) {
        transactionMoneyService.fillTransactionMoneyField(list);
        return ResponseDto.success();
    }
}
