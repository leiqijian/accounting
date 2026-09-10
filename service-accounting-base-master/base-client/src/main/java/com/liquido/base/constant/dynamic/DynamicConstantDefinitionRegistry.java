package com.liquido.base.constant.dynamic;

import java.util.Set;


public interface DynamicConstantDefinitionRegistry {

    DynamicConstantDefinition getDefinition(final String name);

    void registerDefinition(final String name, final DynamicConstantDefinition definition);

    Set<String> getDefinitionNames();
}
