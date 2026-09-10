package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCostBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String feeName;

    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeType;

    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    /**
     * cost volume, Unit: cent
     */
    private BigDecimal volume;

    /**
     * Fee currency
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

}
