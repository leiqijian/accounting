package com.liquido.worker.events.listener;

import java.util.Objects;

import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.events.FeeMonthConfigGenerateEvent;
import com.liquido.worker.service.fee.MonthFeeConfigGenerationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeeMonthConfigGenerateListener {

    private final LarkRobotMonitor larkRobotMonitor;
    private final MonthFeeConfigGenerationService monthFeeConfigGenerationService;

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void feeMonthConfigGenerate(final FeeMonthConfigGenerateEvent event) {
        log.info("[FeeMonthConfigGenerateEvent] start generate fee month config");
        try {
            monthFeeConfigGenerationService.generateMonthFeeConfigEvent(event);
        } catch (Exception e) {
            log.error("[FeeMonthConfigGenerateEvent] generate fee month config error", e);
            final String content = Objects.isNull(event.getTimezone())
                    ? "generate month fee config error"
                    : String.format("timezone: %s\\activeMonth: %s",
                    event.getTimezone(), event.getProductVersionVoList().get(0).getActiveMonth());
            larkRobotMonitor.error("Generate Month Fee Config Error", content, e.getMessage());
        }
    }
}
