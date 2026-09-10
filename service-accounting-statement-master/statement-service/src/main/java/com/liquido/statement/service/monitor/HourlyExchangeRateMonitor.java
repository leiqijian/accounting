package com.liquido.statement.service.monitor;

import java.time.LocalDateTime;
import java.util.List;

import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.service.HourlyExchangeRateService;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service("hourlyExchangeRateMonitor")
public class HourlyExchangeRateMonitor implements StatementMonitor {

    private final LarkRobotMonitor larkRobotMonitor;
    private final HourlyExchangeRateService hourlyExchangeRateService;

    @Override
    public void monitor() {

        final LocalDateTime exchangeTime =
                LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        final List<HourlyExchangeRateDto> hourlyExchangeRates =
                hourlyExchangeRateService.queryHourlyExchangeRate(exchangeTime);

        if (hourlyExchangeRates.isEmpty()) {
            final String content = String.format("Exchange Time: %s\n" + "Info: %s",
                    exchangeTime.format(LocalDateTimeUtil.FORMAT_DATETIME),
                    "The hourly exchange rate is not obtained.");
            larkRobotMonitor.error("Real Time Exchange Rate Get Failed", content, null);
        }

    }
}
