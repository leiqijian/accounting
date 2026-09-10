package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.BusinessTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyTransactionBizBo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    /**
     * totalAmount
     * unit: cent
     */
    private BigDecimal totalAmount;

    /**
     * totalFee
     * unit: cent
     */
    private BigDecimal totalFee;

    /**
     * totalTax
     * unit: cent
     */
    private BigDecimal totalTax;

    /**
     * totalCount
     */
    private Long totalCount;
}
