package com.liquido.worker.events.listener;

import com.liquido.worker.events.CompareMonthConfigEvent;
import com.liquido.worker.service.fee.MonthFeeConfigGenerationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompareMonthConfigListener {

    private final MonthFeeConfigGenerationService monthFeeConfigGenerationService;

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void compareMonthConfigDifferenceAndLastMonth(final CompareMonthConfigEvent event) {
        log.info("compare month config difference and last month");
        monthFeeConfigGenerationService.compareMonthConfigDifferenceAndLastMonth(event);
    }

}
