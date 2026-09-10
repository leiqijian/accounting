package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryTransactionRefundDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uniqueId;

    private String merchantReference;

    private BigDecimal amount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private String tradeTransferStatus;

    private String tradeTransactionType;

    private Long submitTimestamp;

}
