package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DataSyncRefundStatusEnum;
import com.liquido.base.enums.DataSyncStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class PagePaymentLinkDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * payment link unique id
     */
    private String linkId;

    /**
     * merchant's order id
     */
    private String merchantReference;

    private String merchantCode;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    /**
     * save integer type, unit：cent
     */
    private BigDecimal amount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Convert(converter = DataSyncStatusEnum.Convert.class)
    private DataSyncStatusEnum paymentStatus;

    private String settledUniqueId;

    /**
     * timestamp UTC+0
     */
    private LocalDateTime submitTime;

    /**
     * timestamp UTC+0
     */
    private LocalDateTime transactionTime;

    @Convert(converter = DataSyncRefundStatusEnum.Convert.class)
    private DataSyncRefundStatusEnum refundStatus;

    /**
     * save integer type, unit：cent
     */
    private BigDecimal refundAmount;

    private String refundedUniqueId;

    private String subMerchantId;

    private String subMerchantName;

    private String description;

    /**
     * timestamp UTC+0
     */
    private LocalDateTime refundTime;

    @SensitiveField(SensitiveType.EMAIL)
    private String userEmail;

    @SensitiveField(SensitiveType.MOBILE)
    private String userPhone;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
