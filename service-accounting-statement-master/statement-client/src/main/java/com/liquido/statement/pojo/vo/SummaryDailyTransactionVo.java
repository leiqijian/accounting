package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.core.mvc.vo.PageCondition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDailyTransactionVo extends PageCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    @NotNull
    private CountryCodeEnum countryCode;

    @NotBlank
    private String subMerchantId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;


    private Boolean existTransaction;
}
