package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.statement.enums.AdjustmentRevenueRegardEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentReimburseVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * global unique requestId
     */
    @NotBlank
    private String requestId;

    /**
     * merchantId
     */
    @NotNull
    private Long merchantId;

    /**
     * accountId
     */
    @NotNull
    private Long accountId;

    /**
     * unit: cent
     */
    @NotNull
    @Min(1)
    private BigDecimal transactionAmount;

    /**
     * AdjustmentRevenueRegardEnum: whether to register revenue regarded as
     * NONE("NONE", "none revenue"),
     * FEE("FEE", "revenue as extra fee"),
     * TAX("TAX", "revenue as extra tax"),
     */
    @NotNull
    @Convert(converter = AdjustmentRevenueRegardEnum.Convert.class)
    private AdjustmentRevenueRegardEnum revenueRegard;

    /**
     * timezone: UTC 0
     */
    private LocalDateTime transactionTime;

    /**
     * timezone: UTC 0
     */
    private LocalDateTime settlementTime;

    @Length(max = 200)
    private String comments;

}
