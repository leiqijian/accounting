package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.TransactionInProgressStatusEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class TransactionInProgressDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * fk tack_id
     */
    private Long transactionId;

    /**
     * uk_unique_id
     */
    private String uniqueId;

    private Long merchantId;

    private String subMerchantId;

    private Long accountId;

    private String merchantCode;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    private BigDecimal amount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    //pre calculate fee currency =account currency
    private BigDecimal fee;

    //pre calculate tax currency =account currency
    private BigDecimal tax;

    //pre-calculate amount netAmount=amount/rate + fee + tax, currency =account currency
    private BigDecimal netAmount;

    @Convert(converter = TransactionInProgressStatusEnum.Convert.class)
    private TransactionInProgressStatusEnum status;

    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum transactionStatus;

    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum lifecycleStatus;

    private Long lifecycleTimestamp;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    private LocalDateTime createdTime;

}
