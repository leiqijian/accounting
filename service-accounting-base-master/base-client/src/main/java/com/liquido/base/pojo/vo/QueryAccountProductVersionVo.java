package com.liquido.base.pojo.vo;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAccountProductVersionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer activeMonth;

    private LocalDate activeDate;

    private Long accountId;

    private Long accountProductId;

    private List<Long> accountProductIds;

    private Boolean monthlyFlag;

}
