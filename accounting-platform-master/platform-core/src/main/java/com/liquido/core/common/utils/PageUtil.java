package com.liquido.core.common.utils;

import java.util.Objects;
import java.util.stream.Collectors;

import com.liquido.core.mvc.vo.PageCondition;
import com.liquido.core.mvc.vo.PageVo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

@Slf4j
public class PageUtil {

    public static <T, K> PageVo<K> buildPage(final Page<T> page,
                                             final PageCondition vo,
                                             final Class<K> k) {

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), page.getTotalElements(),
                page.getContent().stream().map(v -> {
                    try {
                        final K result = k.getDeclaredConstructor().newInstance();
                        BeanCopierUtil.copyProperties(v, result);
                        return result;
                    } catch (Exception e) {
                        log.error("build pageVo error:", e);
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList()));
    }
}
