package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;
import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class ExtendData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uniqueId;

    // referenceId if is refund transaction, reference original transaction record
    private String referenceId;

    private String referenceNumber;

    private String requestId;

    private String paymentId;

    private String accountId;

    @SensitiveField(SensitiveType.SHIELD)
    private String targetName;

    private String skuCode;

    private Long submitUnixTime;

    private JsonNode baseAmount;

    /**
     * Actual payment amount by end user from transaction-service(virgo)
     * unit:cent
     */
    private BigDecimal paidAmount;

    private String branchId;

    private String bankId;

    private String bankName;

    private String bankCode;

    private String payerCity;

    @SensitiveField(SensitiveType.EMAIL)
    private String payerEmail;

    @SensitiveField(SensitiveType.MOBILE)
    private String payerPhone;

    private String payerComment;

    /**
     * since: just use for payment-link orderId
     */
    private String orderId;

    @SensitiveField(SensitiveType.SHIELD)
    private String cardNumberLast4;

    @SensitiveField(SensitiveType.SHIELD)
    private String cardNumberFirst6;

    private String transferErrorMsg;

    private JsonNode refundAdditionalInfo;

    private Boolean cardUse3ds;

    private Long expirationTime;

    // VISA, MASTERCARD, ELO ...
    private String cardBrand;

    // CREDIT_CARD, DEBIT_CARD
    private String cardType;

    // credit-card installment
    private Integer cardInstallments;

    // transaction is calculated multiple times, sign settle transaction is the final status
    private Boolean stateChange;


    @Converter
    public static class Convert implements AttributeConverter<ExtendData, String> {

        @SneakyThrows
        @Override
        public String convertToDatabaseColumn(final ExtendData others) {
            return Objects.isNull(others) ? null : JsonUtil.toJson(others);
        }

        @SneakyThrows
        @Override
        public ExtendData convertToEntityAttribute(final String dbValue) {
            return StringUtils.isBlank(dbValue)
                    ? null : JsonUtil.toBean(dbValue, ExtendData.class);
        }
    }
}
