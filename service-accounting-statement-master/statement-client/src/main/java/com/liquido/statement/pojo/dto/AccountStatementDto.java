package com.liquido.statement.pojo.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("PMD.TooManyFields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long transactionId;

    private String subMerchantId;

    private Long accountId;

    private Long billId;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    @Convert(converter = AmountPonEnum.Convert.class)
    private AmountPonEnum amountPon;

    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    private LocalDateTime transactionTime;

    private Long transactionTimestamp;

    private LocalDateTime settleTime;

    private BigDecimal amount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private BigDecimal startBalance;

    private BigDecimal endBalance;
}
