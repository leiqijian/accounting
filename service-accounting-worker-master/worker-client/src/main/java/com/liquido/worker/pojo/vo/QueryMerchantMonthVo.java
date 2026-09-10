package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryMerchantMonthVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * yyyyMM
     */
    @NotNull
    private Integer activeMonth;

    private Long merchantId;

    private Long accountId;

    private Long accountProductId;

    private Boolean isAdvance;

}
