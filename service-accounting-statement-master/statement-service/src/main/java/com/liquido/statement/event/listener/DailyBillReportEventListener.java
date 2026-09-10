package com.liquido.statement.event.listener;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.liquido.report.ReportApis;
import com.liquido.report.enums.ReportTypeEnum;
import com.liquido.report.pojo.vo.GenerateDailyReportByBillVo;
import com.liquido.statement.event.DailyCutSuccessEvent;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyBillReportEventListener {
    private final ReportApis.ReportFeign reportServiceFeign;

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void dailyBillReportEventListener(final DailyCutSuccessEvent event) {
        log.info("[DailyCutSuccessEvent] process account daily bill report");

        if (Objects.isNull(event)
                || Objects.isNull(event.getEventArgs())
                || CollectionUtils.isEmpty(event.getEventArgs().getBillList())) {
            return;
        }

        final long start = System.currentTimeMillis();
        log.info("[DailyCutSuccessEvent] start process account daily report. eventArgs={}",
                event.getEventArgs());

        final List<Long> billIdList = event.getEventArgs().getBillList().stream()
                .filter(item -> Objects.nonNull(item) && item.getBillId() > 0)
                .map(DailyCutSuccessBo::getBillId).collect(Collectors.toList());

        reportServiceFeign.generateDailyReportByBill(
                new GenerateDailyReportByBillVo(billIdList, ReportTypeEnum.ACCOUNT));

        log.info("[DailyCutSuccessEvent] end process account daily report. ts={}ms"
                + " billIds={}", (System.currentTimeMillis() - start), billIdList);
    }
}
