package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalAccountTransferOutVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    @NotNull
    private Long subAccountId;

    /**
     * transaction amount, unit:cent
     */
    @NotNull
    @Min(1)
    private BigDecimal amount;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;
}
