package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.OperationMethodEnum;
import com.liquido.base.enums.PaymentChannelEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryPaymentConfigVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long accountId;

    /**
     * operation method
     * 0: auto payment
     * 1: manual payment
     */
    @Convert(converter = OperationMethodEnum.Convert.class)
    private OperationMethodEnum operationMethod;

    @Convert(converter = PaymentChannelEnum.Convert.class)
    private PaymentChannelEnum paymentChannel;

}
