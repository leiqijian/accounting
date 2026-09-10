package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Convert;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryCountryTransactionRatioVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @NotNull
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @NotEmpty
    @Convert(converter = ProductCodeEnum.Convert.class)
    private List<ProductCodeEnum> productCodeList;

}
