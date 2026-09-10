package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwPage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long countLimit;

    private Boolean moreThanLimit;

    private Long totalCount;

    private Long page;

    private Long pageSize;

    private List<T> results;

}
