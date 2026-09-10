package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecalculateCostBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long costId;

    private Long accountId;

    private Long billId;

    private LocalDate transactionDate;

    private Integer activeVersion;

    private Integer bathSize;

    private Set<ProductCodeEnum> productCodes;

}
