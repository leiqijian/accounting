package com.liquido.base.constant.dynamic;

import java.util.List;

public interface DynamicConstantFactory {

    DynamicConstant<?> getConstant(final String name);

    DynamicConstant<?> getConstantOfCode(final Object code);

    List<DynamicConstant<?>> getAllConstant();

    DynamicConstantDefinitionRegistry getRegistry();
}
