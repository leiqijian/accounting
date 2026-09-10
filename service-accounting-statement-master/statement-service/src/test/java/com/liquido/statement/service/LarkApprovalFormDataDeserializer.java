package com.liquido.statement.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Lark Approval FormData Deserializer
 */
public class LarkApprovalFormDataDeserializer extends StdDeserializer<LarkApprovalFormDataBo> {
    public LarkApprovalFormDataDeserializer(final Class<?> vc) {
        super(vc);
    }

    public LarkApprovalFormDataDeserializer() {
        this(null);
    }

    @Override
    public LarkApprovalFormDataBo deserialize(JsonParser jp, DeserializationContext dc)
            throws IOException {
        JsonNode rootNode = jp.getCodec().readTree(jp);
        String type = rootNode.get("type").asText();
        JsonNode valueNode = rootNode.get("value");
        List<LarkApprovalFormDataBo.Option> optionList = Lists.newArrayList();
        List<String> valueList = Lists.newArrayList();
        Object value = null;
        switch (type) {
            case "input":
                value = rootNode.get("value").asText();
                break;

            case "amount":
            case "number":
                value = new BigDecimal(rootNode.get("value").asText());
                break;

            case "radio":
            case "radioV2":
            case "checkbox":
            case "checkboxV2":
                JsonNode option = rootNode.get("option");
                if (option.isArray()) {
                    for (final JsonNode node : option) {
                        optionList.add(dc.readTreeAsValue(node, LarkApprovalFormDataBo.Option.class));
                    }
                } else {
                    optionList.add(dc.readTreeAsValue(option, LarkApprovalFormDataBo.Option.class));
                }
                break;
            case "attachmentV2":
                if (valueNode.isArray()) {
                    for (final JsonNode val : valueNode) {
                        valueList.add(dc.readTreeAsValue(val, String.class));
                    }
                    value = JsonUtil.toJson(valueList);
                }
                break;
            default:
                if (!valueNode.isNull()) {
                    value = rootNode.get("value").asText();
                    if (valueNode.isObject()) {
                        value = JsonUtil.toJson(valueNode);
                    }
                }
                break;
        }

        return LarkApprovalFormDataBo.builder()
                .id(rootNode.get("id").isNull() ? null : rootNode.get("id").asText())
                .name(rootNode.get("name").isNull() ? null : rootNode.get("name").asText())
                .type(rootNode.get("type").isNull() ? null : rootNode.get("type").asText())
                .value(ObjectUtils.isEmpty(value) ? null : value)
                .option(optionList)
                .ext(rootNode.get("ext").isNull() ? null : rootNode.get("ext").asText())
                .build();
    }
}
