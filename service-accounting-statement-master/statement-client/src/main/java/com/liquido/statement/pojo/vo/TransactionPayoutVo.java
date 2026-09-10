package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.PaymentChannelEnum;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPayoutVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long uniqueId;

    @NotNull
    @Convert(converter = PaymentChannelEnum.Convert.class)
    private PaymentChannelEnum paymentChannel;

    @NotNull
    @Min(1)
    private BigDecimal amount;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @NotNull
    private ObjectNode targetInfo;

    @Length(max = 200)
    private String remark;
}
