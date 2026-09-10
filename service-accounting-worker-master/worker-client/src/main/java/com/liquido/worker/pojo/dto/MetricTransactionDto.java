package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricTransactionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionType;

    private LocalDateTime date;

    private Integer countTransaction;

    private BigDecimal sumAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

}
