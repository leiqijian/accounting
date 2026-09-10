package com.liquido.base;

import java.util.Map;

import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Test {
    public static void main(String[] args) {
        final ObjectMapper objectMapper = JsonUtil.getObjectMapper();
        final ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("name", "zhangshan");
        objectNode.put("age", 20);
        objectNode.put("score", 105.8);

        final Map<String, String> info = JsonUtil.toMap(objectNode);

        System.out.println(objectNode);
        System.out.println("=====>>> " + info.get("data"));
        System.out.println(
                "=====>>> " + (objectNode.has("data") ? objectNode.get("data").asText() : "----"));
    }
}
