package com.liquido.core.common.utils;

import com.liquido.core.mvc.enums.SortTypeEnum;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;

public class QueryDslUtil {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static OrderSpecifier<?> genOrderSpecifier(
            final SortTypeEnum sortType, final String sortField) {
        SimplePath<Object> path = Expressions.simplePath(Object.class, sortField);
        return new OrderSpecifier(Order.valueOf(sortType.getCode()), path);
    }
}
