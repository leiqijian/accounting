package com.liquido.base.pojo.vo;

import java.io.Serializable;
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
public class QueryProductMonthlyFeeConfigVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * eg. yyyyMM
     */
    @NotNull
    private Integer activeMonth;

    @NotNull
    private Long accountProductId;

    @NotNull
    private Integer monthlyFeeVersion;

    /**
     * enable calculation rule query, default: true
     */
    @Builder.Default
    private Boolean isCalculationRule = true;

    private Map<String, String> calculationRule;

}
