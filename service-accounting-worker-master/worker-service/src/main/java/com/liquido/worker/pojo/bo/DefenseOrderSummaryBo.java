package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.worker.enums.DefenseStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefenseOrderSummaryBo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Convert(converter = DefenseStatusEnum.Convert.class)
    private DefenseStatusEnum defenseStatus;

    private Long count;

    private BigDecimal amount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

}
