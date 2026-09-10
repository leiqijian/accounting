package com.liquido.statement.event.listener;

import java.util.Objects;

import com.liquido.statement.event.DailyCutSuccessEvent;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;
import com.liquido.statement.service.AccountDailyInitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Generate the account bill init info(account_daily_init) of the next day after daily-cut success
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitDailyBillEventListener {

    private final AccountDailyInitService accountDailyInitService;

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void initDailyBillEventListener(final DailyCutSuccessEvent event) {
        log.info("[DailyCutSuccessEvent] initialization next daily bill info");
        if (Objects.isNull(event)
                || Objects.isNull(event.getEventArgs())
                || CollectionUtils.isEmpty(event.getEventArgs().getBillList())) {
            return;
        }

        final long start = System.currentTimeMillis();
        log.info("[DailyCutSuccessEvent] start process account daily bill info init. eventArgs={}",
                event.getEventArgs());

        for (final DailyCutSuccessBo data : event.getEventArgs().getBillList()) {
            try {
                if (Objects.nonNull(data)) {
                    // generic next daily bill init info
                    accountDailyInitService.getDailyBillInitInfo(data.getAccountId(),
                            data.getBillDate().plusDays(1));
                }
            } catch (Exception e) {
                log.error("[DailyCutSuccessEvent] process account daily bill info init error."
                        + " info={}", data, e);
            }
        }

        log.info("[DailyCutSuccessEvent] end process account daily bill info init. ts={}ms",
                (System.currentTimeMillis() - start));
    }
}
