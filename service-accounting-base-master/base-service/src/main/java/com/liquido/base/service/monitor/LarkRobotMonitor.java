package com.liquido.base.service.monitor;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.liquido.base.common.properties.LarkProperties;
import com.liquido.core.common.security.Sha256Util;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.OkHttpClientUtil;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LarkRobotMonitor {

    private final LarkProperties.AlarmRobot properties;

    public static final String ROBOT_EMERGENCY_TPL = "robot_emergency_tpl.json";

    @Async("monitorExecutor")
    @SneakyThrows
    public void info(final String title, final String info,
                     final String detail) {
        this.monitor("BLUE", "[INFO] " + title, info, detail);
    }

    @Async("monitorExecutor")
    @SneakyThrows
    public void warn(final String title, final String info,
                     final String detail) {
        this.monitor("YELLOW", "[WARN] " + title, info, detail);
    }

    @Async("monitorExecutor")
    @SneakyThrows
    public void error(final String title, final String info,
                      final String detail) {
        this.monitor("RED", "[ERROR] " + title, info, detail);
    }

    public void monitor(final String headColor, final String title,
                        final String info, final String detail) {

        final String timeNow = Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime()
                .format(LocalDateTimeUtil.FORMAT_DATETIME);

        final String template = LarkRobotTemplate.getInstance()
                .getLarkTemplate(ROBOT_EMERGENCY_TPL);

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
        dataMap.put("sign",
                Sha256Util.getRobotSign(properties.getSignKey(), timestamp));
        dataMap.put("msg_type", "interactive");
        dataMap.put("card", content);

        OkHttpClientUtil.postWithJson(properties.getWebhook(), dataMap);
    }
}
