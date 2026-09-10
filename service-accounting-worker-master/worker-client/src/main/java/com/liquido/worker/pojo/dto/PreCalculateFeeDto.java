package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
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
public class PreCalculateFeeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long transactionId;

    private String uniqueId;

    /**
     * merchant code: examples cheng_fan
     */
    private String merchantCode;

    /**
     * subMerchantId
     */
    private String subMerchantId;

    /**
     * merchant reference
     */
    private String merchantReference;

    private BigDecimal amount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * SPEI/TED/PIX/CREDIT_CARD/BOLETO/OXXO
     */
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    /**
     * SETTLED/REFUND/CHARGE_BACK
     */
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum transactionStatus;

    private Long lifecycleTimestamp;

    /**
     * Shown status: lifecycleStatus
     */
    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum lifecycleStatus;

    /**
     * vendor
     */
    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    private BigDecimal settleAmount;

    private BigDecimal merchantRate;

    private List<TransactionFeeDto> transactionFeeList;

}
