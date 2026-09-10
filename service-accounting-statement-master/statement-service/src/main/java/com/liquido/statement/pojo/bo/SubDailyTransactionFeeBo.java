package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubDailyTransactionFeeBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long accountId;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    /**
     * totalCount
     */
    private Long totalCount;

    /**
     * actuality calculate fee amount,
     * unit: cent(keep 6 decimal places)
     */
    private BigDecimal totalCalculateAmount;

    /**
     * total settled fee amount,
     * unit: cent(keep 0 decimal places)
     */
    private BigDecimal totalSettlementAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;
}
