package com.liquido.core.mvc.interceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import com.liquido.core.common.constant.Constant;
import com.liquido.core.common.logger.LogConstant;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.mvc.annotation.DisableGetTransferPojo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

/**
 * Feign Interceptor Trace
 */
@SuppressWarnings({"SpringJavaAutowiredMembersInspection"})
@RequiredArgsConstructor
public class CustomizeFeignInterceptor implements RequestInterceptor {
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String appName;

    @SneakyThrows
    @Override
    public void apply(final RequestTemplate template) {
        String traceId = MDC.get(LogConstant.TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = DataUtil.getUuid();
        }
        template.header(LogConstant.TRACE_ID, traceId);
        template.header(Constant.Header.SERVICE_FROM, appName);

        /* support GET method to transfer pojo params */
        final DisableGetTransferPojo disableGetTransferPojo = template.methodMetadata().targetType()
                .getAnnotation(DisableGetTransferPojo.class);
        if (HttpMethod.GET.name().equalsIgnoreCase(template.method())
                && Objects.isNull(disableGetTransferPojo)
                && Objects.nonNull(template.body())) {
            final JsonNode jsonNode = objectMapper.readTree(template.body());
            template.body(null, null);
            Map<String, Collection<String>> queries = new HashMap<>();
            buildQuery(jsonNode, "", queries);
            template.queries(queries);
        }
    }

    private void buildQuery(final JsonNode jsonNode,
                            final String path,
                            final Map<String, Collection<String>> queries) {
        if (!jsonNode.isContainerNode()) {
            if (jsonNode.isNull()) {
                return;
            }
            Collection<String> values = queries.get(path);
            if (Objects.isNull(values)) {
                values = new ArrayList<>();
                queries.put(path, values);
            }
            values.add(jsonNode.asText());
            return;
        }

        if (jsonNode.isArray()) {
            for (final JsonNode node : jsonNode) {
                buildQuery(node, path, queries);
            }
        } else {
            final Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields();
            while (it.hasNext()) {
                final Map.Entry<String, JsonNode> entry = it.next();
                if (StringUtils.isNotBlank(path)) {
                    buildQuery(entry.getValue(), path + "." + entry.getKey(), queries);
                } else {
                    buildQuery(entry.getValue(), entry.getKey(), queries);
                }
            }
        }
    }
}
