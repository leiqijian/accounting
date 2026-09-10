package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.enums.CalculationTaskStateEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class PageTransactionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uniqueId;

    @Convert(converter = CalculationTaskStateEnum.Convert.class)
    private CalculationTaskStateEnum taskStatus;

    private String merchantReference;

    private String merchantCode;

    private String merchantName;

    private String accountName;

    /**
     * date of the country timezone(Submit Time)
     */
    private LocalDateTime date;

    /**
     * date of the country timezone(Transaction Time)
     */
    private LocalDateTime transactionDate;

    private BigDecimal amount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum product;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionType;

    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum transactionStatus;

    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum status;

    private String tradeTransferStatus;

    private String tradeTransactionType;

    private Boolean holdStatus;

    private String subMerchantId;

    private String subMerchantName;

    private String description;

}
