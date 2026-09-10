package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAccountMonthlyFeeConfigVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long accountId;

    @NotNull
    private LocalDate activeDate;

    private Map<String, String> calculationRule;

}
