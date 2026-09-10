package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
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

@SuppressWarnings("PMD.TooManyFields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionInProgressBo implements Serializable {

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

    private BigDecimal fee;

    private BigDecimal tax;

    private BigDecimal netAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

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

    /**
     * Indicates if the amount needs to be changed.
     */
    private Boolean isChangedAmount;
}
