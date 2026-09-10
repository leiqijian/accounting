package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDailyTransactionBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long billId;

    private BigDecimal totalTransactionAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionCurrency;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;
}
