package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.statement.enums.GlobalAccountStateEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalAccountDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    /**
     * global balance, unit:cent
     */
    private BigDecimal globalBalance;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Convert(converter = GlobalAccountStateEnum.Convert.class)
    private GlobalAccountStateEnum state;
}
