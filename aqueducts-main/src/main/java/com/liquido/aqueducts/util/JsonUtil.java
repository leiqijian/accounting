package com.liquido.aqueducts.util;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    public static JsonNode parseJson(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        return Optional.ofNullable(jsonString)
                .map(str -> {
                    try {
                        return objectMapper.readTree(str);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElse(null);
    }

    public static String getJsonText(JsonNode jsonNode, String key) {
        return Optional.ofNullable(jsonNode)
                .map(node -> {
                    if (Objects.isNull(node.get(key))) {
                        return null;
                    } else {
                        return node.get(key).asText();
                    }
                })
                .orElse(null);
    }


    public static String mapToJsonString(Map<String, String[]> map) {
        ObjectMapper objectMapper = new ObjectMapper();

        return Optional.ofNullable(map).map(obj -> {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException ignore) {
                return null;
            }
        }).orElse(null);
    }
}
