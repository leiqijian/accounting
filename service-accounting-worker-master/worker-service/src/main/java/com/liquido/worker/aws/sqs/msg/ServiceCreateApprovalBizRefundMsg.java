package com.liquido.worker.aws.sqs.msg;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreateApprovalBizRefundMsg {

    /**
     * merchant info: code
     */
    private String merchantCode;

    /**
     * country: CountryCodeEnum
     */
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * transaction type: TransactionTypeCodeEnum
     */
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * merchant order id
     */
    private String merchantReference;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private BigDecimal refundAmount;

    /**
     * refund time (UTC 0)
     */
    private LocalDateTime refundTime;

    private ObjectNode refundAdditionalInfo;

    private Boolean manualRefund;

}
