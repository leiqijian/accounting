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
public class BatchWithdrawalApprovedHandleDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long transactionId;

    private String subMerchantId;

    private BigDecimal withdrawalAmount;

    private BigDecimal feeAmount;

    private BigDecimal taxAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

}
