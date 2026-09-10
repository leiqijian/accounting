package com.liquido.statement.event.listener;

import java.util.Objects;

import com.liquido.statement.event.DailyCutSuccessEvent;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;
import com.liquido.statement.service.TransactionSummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitTransactionSummaryEventListener {

    private final TransactionSummaryService transactionSummaryService;

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void initDailyBillEventListener(final DailyCutSuccessEvent event) {
        log.info("[DailyCutSuccessEvent] initialization next transaction summary");
        if (Objects.isNull(event)
                || Objects.isNull(event.getEventArgs())
                || CollectionUtils.isEmpty(event.getEventArgs().getBillList())) {
            return;
        }

        final long start = System.currentTimeMillis();
        log.info("[DailyCutSuccessEvent] start process transaction summary info init. eventArgs={}",
                event.getEventArgs());

        for (final DailyCutSuccessBo data : event.getEventArgs().getBillList()) {
            try {
                if (Objects.nonNull(data)) {
                    transactionSummaryService.getOrInitTransactionSummary(
                            data.getMerchantId(),
                            data.getAccountId(),
                            data.getBillDate().plusDays(1));
                }
            } catch (Exception e) {
                log.error("[DailyCutSuccessEvent] process transaction summary init error."
                        + " info={}", data, e);
            }
        }

        log.info("[DailyCutSuccessEvent] end process transaction summary info init. ts={}ms",
                (System.currentTimeMillis() - start));
    }
}
