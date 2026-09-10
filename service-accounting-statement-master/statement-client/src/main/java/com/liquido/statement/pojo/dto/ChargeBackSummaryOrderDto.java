package com.liquido.statement.pojo.dto;

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
public class ChargeBackSummaryOrderDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long defenseWonCount;

    private BigDecimal defenseWonAmount;

    private Long underDefenseCount;

    private BigDecimal underDefenseAmount;

    private Long totalCount;

    private BigDecimal totalAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;
}
