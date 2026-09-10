package com.liquido.statement.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.Constant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.support.CronExpression;


public class MainTest {
    private static final ZoneId zoneId = Constant.COMMON.ZONE_UTC;
    private static final String utczone = "UTC";
    private static final String timezone5 = "UTC-5";
    private static final String timezone8 = "UTC+8";

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            System.out.println(RandomStringUtils.randomNumeric(6));
        }

        final LocalDateTime utcNow = LocalDateTimeUtil.formatToLocalDateTime("2024-07-15 13:01:45");

        // UTC+0 Cron
        final String cron = "* 0 13 * * ?";

        // UTC+0 now
        final LocalDateTime utcTimeNow = utcNow.withSecond(0).withNano(0);

        final LocalDateTime cronTimeNow = CronExpression.parse(cron).next(utcTimeNow)
                .withSecond(0).withNano(0);

        System.out.println("utcTimeNow= " +
                utcTimeNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("cronTimeNow=" +
                cronTimeNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println(utcTimeNow.equals(cronTimeNow));

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestBo {
        // payin refund, charge_back, charge_back_rejected;
        private List<Integer> needList;

        // all original transaction money list
        private List<Integer> allList;
    }
}
