package com.liquido.statement.service.monitor.lark;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.liquido.core.common.security.Sha256Util;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.OkHttpClientUtil;
import com.liquido.statement.common.properties.StatementProperties;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LarkRobotMonitor {

    private final StatementProperties.LarkProperties properties;

    @Async("monitorExecutor")
    @SneakyThrows
    public void info(final String title, final String info,
                     final String detail) {
        this.monitor("BLUE", "[INFO] " + title, info, detail);
    }

    @Async("monitorExecutor")
    @SneakyThrows
    public void info(final String title, final String info,
                     final String detail, final String webhook,
                     final String signKey) {
        this.monitor("BLUE", "[INFO] " + title, info, detail, webhook, signKey);
    }

    @Async("monitorExecutor")
    @SneakyThrows
    public void warn(final String title, final String info,
                     final String detail) {
        this.monitor("YELLOW", "[WARN] " + title, info, detail);
    }

    @Async("monitorExecutor")
    @SneakyThrows
    public void warn(final String title, final String info,
                     final String detail, final String webhook,
                     final String signKey) {
        this.monitor("YELLOW", "[WARN] " + title, info, detail, webhook, signKey);
    }

    @Async("monitorExecutor")
    @SneakyThrows
    public void error(final String title, final String info,
                      final String detail) {
        this.monitor("RED", "[ERROR] " + title, info, detail);
    }

    @Async("monitorExecutor")
    @SneakyThrows
    public void error(final String title, final String info,
                      final String detail, final String webhook,
                      final String signKey) {
        this.monitor("RED", "[ERROR] " + title, info, detail, webhook, signKey);
    }


    public void monitor(final String headColor, final String title,
                        final String info, final String detail, final String webhook,
                        final String signKey) {

        final String timeNow = Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime()
                .format(LocalDateTimeUtil.FORMAT_DATETIME);

        final String template = LarkRobotTemplate.getInstance()
                .getLarkTemplate(LarkRobotTemplate.ROBOT_EMERGENCY_TPL);

        final String content = new StringSubstitutor(
                Map.of("headerColour", headColor,
                        "title", title,
                        "content", Optional.ofNullable(info).orElse(""),
                        "timeNow", timeNow,
                        "remark", StringEscapeUtils.escapeJson(Optional.ofNullable(detail)
                                .orElse("")))
        ).replace(template);

        final long timestamp = LocalDateTimeUtil.utcToInstant(LocalDateTimeUtil.nowUtc());
        final Map<String, Object> dataMap = new HashMap<>(4);
        dataMap.put("timestamp", timestamp);
        dataMap.put("sign", Sha256Util.getRobotSign(StringUtils.isBlank(signKey)
                ? properties.getAlarmRobot().getSignKey()
                : signKey, timestamp));
        dataMap.put("msg_type", "interactive");
        dataMap.put("card", content);

        try {
            OkHttpClientUtil.postWithJson(
                    StringUtils.isBlank(webhook) ? properties.getAlarmRobot().getWebhook() :
                            webhook, dataMap);
        } catch (Exception e) {
            log.error("lark notify error: ", e);
        }
    }

    public void monitor(final String headColor, final String title,
                        final String info, final String detail) {

        this.monitor(headColor, title, info, detail, null, null);
    }
}