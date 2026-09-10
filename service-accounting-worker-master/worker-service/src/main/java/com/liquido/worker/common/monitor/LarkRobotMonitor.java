package com.liquido.worker.common.monitor;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.liquido.core.common.security.Sha256Util;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.OkHttpClientUtil;
import com.liquido.worker.common.properties.LarkProperties;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LarkRobotMonitor {

    private final LarkProperties.AlarmRobotProperties properties;

    @SneakyThrows
    @Async("monitorExecutor")
    public void info(final String title, final String info, final String detail) {

        this.monitor("BLUE", "[INFO] " + title, info, detail);
    }

    @SneakyThrows
    @Async("monitorExecutor")
    public void warn(final String title, final String info, final String detail) {

        this.monitor("YELLOW", "[WARN] " + title, info, detail);
    }

    @SneakyThrows
    @Async("monitorExecutor")
    public void error(final String title, final String info, final String detail) {

        this.monitor("RED", "[ERROR] " + title, info, detail);
    }

    @SneakyThrows
    @Async("monitorExecutor")
    public void monitor(final String headColor, final String title,
                        final String info, final String detail) {

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
        dataMap.put("sign", Sha256Util.getRobotSign(properties.getSignKey(), timestamp));
        dataMap.put("msg_type", "interactive");
        dataMap.put("card", content);

        OkHttpClientUtil.postWithJson(properties.getWebhook(), dataMap);
    }


    public String buildLarkAlarmContent(final String merchantName, final String countryCode,
                                        final String transactionType, final String msgInfo) {

        return buildLarkAlarmContent(merchantName, countryCode, transactionType, "", msgInfo);
    }

    public String buildLarkAlarmContent(final String merchantName, final String countryCode,
                                        final String transactionType, final String uniqueId,
                                        final String msgInfo) {

        StringBuilder sb = new StringBuilder();

        sb = StringUtils.isNotBlank(merchantName)
                ? sb.append("**Merchant:** ").append(merchantName).append("\\n") : sb;

        sb = StringUtils.isNotBlank(countryCode)
                ? sb.append("**Country:** ").append(countryCode).append("\\n") : sb;

        sb = StringUtils.isNotBlank(transactionType)
                ? sb.append("**Transaction Type:** ").append(transactionType).append("\\n") : sb;

        sb = StringUtils.isNotBlank(uniqueId)
                ? sb.append("**UniqueId|LinkId:** ").append(uniqueId).append("\\n") : sb;

        sb = StringUtils.isNotBlank(msgInfo)
                ? sb.append("**Monitor Info:** ").append(msgInfo).append("\\n") : sb;

        return sb.toString();
    }
}
