package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.FeeGroupEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyTransactionFeeBo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    /**
     * actuality calculate fee amount,
     * unit: cent(keep 6 decimal places)
     */
    private BigDecimal totalCalculateAmount;

    /**
     * total settled fee amount,
     * unit: cent(keep 0 decimal places)
     */
    private BigDecimal totalAmount;

    /**
     * instantFlag
     * true: instant settlement;
     * false: non-instant settlement;
     */
    private Boolean instantFlag;

    /**
     * totalCount
     */
    private Long totalCount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;
}
