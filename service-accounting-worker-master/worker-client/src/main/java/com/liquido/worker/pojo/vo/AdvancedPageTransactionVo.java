package com.liquido.worker.pojo.vo;

import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.mvc.vo.PageCondition;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AdvancedPageTransactionVo extends PageCondition {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String merchantCode;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    @NotNull
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionType;

    @NotNull
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum product;

    /**
     * start date of the UTC
     */
    @NotNull
    private LocalDateTime startDate;

    /**
     * end date of the UTC
     */
    private LocalDateTime endDate;

    /**
     * advanced query params
     */
    private JsonNode other;

}
