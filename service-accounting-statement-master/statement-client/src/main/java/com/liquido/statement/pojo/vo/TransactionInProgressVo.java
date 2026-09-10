package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
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
public class TransactionInProgressVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * fk tack_id
     */
    @NotNull
    private Long transactionId;

    /**
     * uk_unique_id
     */
    @NotEmpty
    private String uniqueId;

    @NotNull
    private String merchantCode;

    /**
     * subMerchantId
     */
    private String subMerchantId;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @NotNull
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @NotNull
    private BigDecimal amount;

    @NotNull
    //pre calculate fee currency =account currency
    private BigDecimal fee;

    @NotNull
    //pre calculate tax currency =account currency
    private BigDecimal tax;

    @NotNull
    //pre-calculate amount netAmount=amount/rate + fee + tax, currency =account currency
    private BigDecimal netAmount;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @NotNull
    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum transactionStatus;

    @NotNull
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @NotNull
    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum lifecycleStatus;

    @NotNull
    private Long lifecycleTimestamp;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

}
