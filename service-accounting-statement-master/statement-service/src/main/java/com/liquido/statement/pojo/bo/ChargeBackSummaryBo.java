package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.statement.enums.TransactionChargeBackStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeBackSummaryBo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Convert(converter = TransactionChargeBackStatusEnum.Convert.class)
    private TransactionChargeBackStatusEnum status;

    private Long count;

    private BigDecimal amount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

}
