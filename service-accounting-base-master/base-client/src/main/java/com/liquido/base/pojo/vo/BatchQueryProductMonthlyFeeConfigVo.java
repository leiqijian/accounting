package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchQueryProductMonthlyFeeConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * eg. yyyyMM
     */
    @NotNull
    private Integer activeMonth;

    private Long accountId;

    @NotEmpty
    private List<ProductMonthlyFeeConfigVo> queryVoList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductMonthlyFeeConfigVo implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long accountProductId;

        private Integer monthlyFeeVersion;

        private Map<String, String> calculationRule;

    }

}
