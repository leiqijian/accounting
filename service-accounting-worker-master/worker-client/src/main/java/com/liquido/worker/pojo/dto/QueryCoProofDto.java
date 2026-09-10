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
public class QueryCoProofDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String number;

    private String targetName;

    private String documentId;

    private String targetAccountId;

    private String bankName;

    private BigDecimal amount;

    private String targetAccountType;

}
