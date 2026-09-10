package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CreditCardGroupCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryCreditCardGroupVo implements Serializable {
    private static final long serialVersionUID = 1;

    @NotNull
    @Convert(converter = CreditCardGroupCodeEnum.Convert.class)
    private CreditCardGroupCodeEnum groupCode;
}
