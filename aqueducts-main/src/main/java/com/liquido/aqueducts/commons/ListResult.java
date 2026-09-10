package com.liquido.aqueducts.commons;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListResult<T> {

    private long totalCount;

    private List<T> results;

    public static ListResult empty() {
        return ListResult.builder()
                .totalCount(0)
                .results(new ArrayList<>())
                .build();
    }

}
