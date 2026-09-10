package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.BusinessTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRollBackVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum bizTypeCode;

    @NotBlank
    private String requestId;

    @NotNull
    @Min(1)
    private Long transactionId;

    @NotNull
    @Min(1)
    private Long merchantId;

    private String subMerchantId;

    @NotNull
    @Min(1)
    private Long accountId;

    /**
     * apply withdrawal Amount
     * unit: cent
     */
    @NotNull
    @Min(1)
    private BigDecimal amount;
}
