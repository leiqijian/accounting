package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.PaymentProofEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class QueryTransactionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String uniqueId;

    private String documentId;

    private String merchantCode;

    private String merchantName;

    private String merchantReference;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum product;

    private String subProduct;

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

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    private String accountName;

    private BigDecimal amount;

    private String transferErrorMsg;

    private Long createTime;

    private Boolean isSupportProofDownload;

    @Convert(converter = PaymentProofEnum.Convert.class)
    private PaymentProofEnum paymentProof;

    private JsonNode lifeCycle;

    private List<QueryTransactionRefundDto> refundList;

    private BigDecimal refundTotalAmount;

    private JsonNode feeOthers;

    private Map<String, JsonNode> detail;

    private String subMerchantId;

    private String subMerchantName;

}
