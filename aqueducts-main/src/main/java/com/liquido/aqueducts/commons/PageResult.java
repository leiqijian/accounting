package com.liquido.aqueducts.commons;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {

    public final static long COUNT_LIMIT = 1000L;

    private long countLimit;

    private boolean moreThanLimit;

    private long totalCount;

    private long page;

    private long pageSize;

    private List<T> results;

    public static PageResult empty() {
        return PageResult.builder()
                .countLimit(COUNT_LIMIT)
                .moreThanLimit(false)
                .totalCount(0)
                .page(1)
                .pageSize(20)
                .results(new ArrayList<>())
                .build();
    }

}
