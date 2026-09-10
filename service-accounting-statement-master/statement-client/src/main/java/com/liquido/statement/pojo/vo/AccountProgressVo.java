package com.liquido.statement.pojo.vo;


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
public class AccountProgressVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    private Long accountId;

    private BigDecimal inProgressAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum inProgressCurrency;

}
