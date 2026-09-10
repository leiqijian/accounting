package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.manage.BizTopupManager;
import com.liquido.statement.manage.BizTransferOutManager;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountTransferConfig;
import com.liquido.statement.pojo.entity.AccountTransferRecord;
import com.liquido.statement.pojo.vo.AccountTransferVo;
import com.liquido.statement.pojo.vo.TransactionBizVo;
import com.liquido.statement.service.AccountTransferRecordService;
import com.liquido.statement.service.InternalAccountTransferService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalAccountTransferServiceImpl implements InternalAccountTransferService {
    private final BizTopupManager bizTopupManager;
    private final BizTransferOutManager bizTransferOutManager;
    private final AccountTransferRecordService accountTransferRecordService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void executeInternalTransfer(final Account payinAccount,
                                        final Account payoutAccount,
                                        final AccountTransferVo param,
                                        final AccountTransferConfig config) {

        final LocalDateTime timeNow = LocalDateTimeUtil.nowUtc();
        final BigDecimal paymentAmount = payinAccount.getExtractableBalance()
                .compareTo(param.getTransactionAmount()) >= 0
                ? param.getTransactionAmount() : payinAccount.getExtractableBalance();

        log.info("execute internal transfer payinAccountId={}, payoutAccountId={}, "
                        + "extractableBalance={}, transactionAmount={}", payinAccount.getId(),
                payoutAccount.getId(), payinAccount.getExtractableBalance(),
                param.getTransactionAmount());

        final Long orderId = SnowflakeIdUtil.generate();
        /* Step:1 Save internal account transfer record */
        accountTransferRecordService.saveTransferOrder(AccountTransferRecord.builder()
                .id(orderId)
                .merchantId(payinAccount.getMerchantId())
                .payinAccountId(payinAccount.getId())
                .payoutAccountId(payoutAccount.getId())
                .transactionAmount(paymentAmount)
                .transactionTime(Optional.ofNullable(param.getTransactionTime())
                        .orElse(LocalDateTimeUtil.nowUtc()))
                .operateMode(param.getOperateMode())
                .createdTime(timeNow)
                .updatedTime(timeNow)
                .version(1)
                .build());

        /* Step:2 Generate payin account biz_transfer_out record */
        bizTransferOutManager.executeTransferOut(TransactionBizVo.builder()
                .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                .accountId(payinAccount.getId())
                .operateMode(param.getOperateMode())
                .transactionAmount(paymentAmount)
                .transactionCurrency(payinAccount.getCurrency())
                .settlementAmount(paymentAmount)
                .settlementCurrency(payinAccount.getCurrency())
                .exchangeRate(BigDecimal.ONE)
                .feeAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .comments("internal auto transfer out")
                .transactionTime(param.getTransactionTime())
                .settlementTime(param.getSettlementTime())
                .remark(orderId.toString())
                .createdBy(0L).build(), payinAccount);

        /* Step:3 Generate payout account biz_topup record */
        bizTopupManager.executeTopUp(TransactionBizVo.builder()
                .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                .accountId(payoutAccount.getId())
                .operateMode(param.getOperateMode())
                .transactionAmount(paymentAmount)
                .transactionCurrency(payinAccount.getCurrency())
                .settlementAmount(paymentAmount)
                .settlementCurrency(payinAccount.getCurrency())
                .exchangeRate(BigDecimal.ONE)
                .feeAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .comments("internal auto topup")
                .transactionTime(param.getTransactionTime())
                .settlementTime(param.getSettlementTime())
                .remark(orderId.toString())
                .createdBy(0L).build(), payoutAccount);
    }
}
