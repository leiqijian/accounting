package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Convert;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAlarmConfigBo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Boolean flag;

    @NotNull
    private BigDecimal amountLimit;

    @Convert(converter = CurrencyEnum.Convert.class)
    @NotNull
    private CurrencyEnum currencyEnum;

    private Integer alarmCycleHours;

    @SensitiveField(SensitiveType.EMAIL)
    @NotEmpty
    private List<String> email;

}
