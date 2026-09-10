package com.liquido.base.constant.dynamic;

import java.util.Map;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DynamicConstantDefinition {

    private final String name;

    private final String className;

    private final Map<String, String> properties;
}
