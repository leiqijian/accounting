package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.enums.CalculationTaskStateEnum;
import com.liquido.worker.enums.CalculationTaskTypeEnum;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class TaskFeeCalculationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * Payment Platform Unique ID
     */
    private String uniqueId;

    /**
     * documentId certificate No, e.g: CPF, CNPJ
     */
    private String documentId;

    /**
     * holdStatus false:OFF, true:ON
     */
    @Convert(converter = HoldStatusEnum.Convert.class)
    private HoldStatusEnum holdStatus;

    /**
     * UNREPEATABLE/REPEATABLE
     */
    @Convert(converter = CalculationTaskTypeEnum.Convert.class)
    private CalculationTaskTypeEnum taskType;

    /**
     * WAITING/PROCESSING/SUCCESS/FAILED
     */
    @Convert(converter = CalculationTaskStateEnum.Convert.class)
    private CalculationTaskStateEnum taskStatus;

    private String merchantCode;

    private String merchantName;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
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

    /**
     * Merchant reference idempotent_key
     */
    private String merchantReference;

    /**
     * payee/payer
     */
    private String accountName;

    /**
     * save integer type, unit：cent
     */
    private BigDecimal amount;

    /**
     * MXN/BRL/USD
     */
    private CurrencyEnum currency;

    /**
     * transaction status final time
     */
    private LocalDateTime transactionTime;

    private Long transactionTimestamp;

    private Long submitTimestamp;

    private TransactionStatusEnum transactionStatus;

    private String comments;

    private ObjectNode others;

}
