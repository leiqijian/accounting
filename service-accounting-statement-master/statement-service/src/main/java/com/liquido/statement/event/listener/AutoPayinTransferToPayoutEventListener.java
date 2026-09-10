package com.liquido.statement.event.listener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.base.enums.OperateModeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.event.DailyCutSuccessEvent;
import com.liquido.statement.manage.InternalAccountTransferManager;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;
import com.liquido.statement.pojo.vo.AccountTransferVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Within the same merchant Payin account extractable balance
 * auto transferred to Payout account balance
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoPayinTransferToPayoutEventListener {

    private final InternalAccountTransferManager internalAccountTransferManager;

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void autoPayinTransferToPayoutEventListener(final DailyCutSuccessEvent event) {
        log.info("[DailyCutSuccessEvent] Payin extractable balance auto transfer to "
                + "Payout account balance");
        if (Objects.isNull(event)
                || Objects.isNull(event.getEventArgs())
                || Boolean.FALSE.equals(event.getEventArgs().getAutoPayment())
                || CollectionUtils.isEmpty(event.getEventArgs().getBillList())) {
            return;
        }

        final long start = System.currentTimeMillis();
        log.info("[DailyCutSuccessEvent] start process account balance auto transfer. eventArgs={}",
                event.getEventArgs());

        final List<DailyCutSuccessBo> payInAccList = event.getEventArgs().getBillList().stream()
                .filter(item -> TransactionTypeCodeEnum.PAY_IN == item.getTransactionTypeCode())
                .collect(Collectors.toList());

        AccountTransferVo params = null;
        for (final DailyCutSuccessBo bo : payInAccList) {
            try {

                final BigDecimal transactionAmount = Optional.ofNullable(
                                bo.getDailyExtractableAmountInfo()
                                        .getLatestDailyExtractableEndBalance())
                        .orElse(BigDecimal.ZERO).add(Optional.ofNullable(
                                        bo.getDailyExtractableAmountInfo()
                                                .getCurrentDailyTnExtractableAmount())
                                .orElse(BigDecimal.ZERO));
                if (transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                final LocalDateTime nowTime = LocalDateTimeUtil.nowUtc();
                params = AccountTransferVo.builder()
                        .merchantId(bo.getMerchantId())
                        .payinAccountId(bo.getAccountId())
                        .transactionAmount(transactionAmount)
                        .operateMode(OperateModeEnum.AUTO)
                        .transactionTime(nowTime)
                        .settlementTime(nowTime)
                        .build();

                log.info("begin process internal account transfer params={}", params);
                internalAccountTransferManager.defaultProcessInternalAccountTransfer(params);
                log.info("end process internal account transfer params={}", params);

            } catch (Exception e) {
                log.error("process internal account transfer params={}, error:", params, e);
            }
        }

        log.info("[DailyCutSuccessEvent] end process account balance auto transfer. ts={}ms",
                (System.currentTimeMillis() - start));
    }
}
