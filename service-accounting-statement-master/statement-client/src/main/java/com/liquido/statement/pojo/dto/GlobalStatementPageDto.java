package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.GlobalTargetTypeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalStatementPageDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    @Convert(converter = GlobalTargetTypeEnum.Convert.class)
    private GlobalTargetTypeEnum targetType;

    /**
     * transaction amount, unit:cent
     */
    private BigDecimal amount;

    private BigDecimal startBalance;

    private BigDecimal endBalance;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    /**
     * subAccount belong country
     */
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * subAccount belong transactionTypeCode
     */
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;
}
