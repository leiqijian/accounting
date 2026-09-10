package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.liquido.base.enums.ExtraFeeGroupEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionExtraIncomeBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private ExtraFeeGroupEnum extraFeeGroup;

    /**
     * extra income/fee volume, Unit: cent, Currency: USD
     */
    private BigDecimal volume;
}
