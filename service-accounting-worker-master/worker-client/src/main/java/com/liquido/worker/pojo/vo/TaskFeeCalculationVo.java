package com.liquido.worker.pojo.vo;

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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"merchantCode", "countryCode", "transactionTypeCode"})
@SuppressWarnings("PMD.TooManyFields")
public class TaskFeeCalculationVo {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String uniqueId;

    private String documentId;

    @Convert(converter = HoldStatusEnum.Convert.class)
    private HoldStatusEnum holdStatus;

    @Convert(converter = CalculationTaskTypeEnum.Convert.class)
    private CalculationTaskTypeEnum taskType;

    @Convert(converter = CalculationTaskStateEnum.Convert.class)
    private CalculationTaskStateEnum taskStatus;

    private String merchantCode;

    private String merchantName;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    private String merchantReference;

    private BigDecimal amount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private LocalDateTime transactionTime;

    private Long transactionTimestamp;

    private Long submitTimestamp;

    @Convert(converter = TransactionStatusEnum.Convert.class)
    private TransactionStatusEnum transactionStatus;

    private String comments;

    private ObjectNode others;

    private LocalDateTime eventTime;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Long createdBy;

    private Long updatedBy;

    private Integer version;

    private Boolean delFlag;
}
