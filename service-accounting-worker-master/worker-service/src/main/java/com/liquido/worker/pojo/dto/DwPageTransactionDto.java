package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwPageTransactionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uniqueId;

    private String merchantReference;

    private String status;

    private String productCode;

    private BigDecimal amount;

    private String currency;

    private String country;

    private Long timestamp;

}
