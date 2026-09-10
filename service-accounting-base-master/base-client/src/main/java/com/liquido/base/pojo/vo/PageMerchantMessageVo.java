package com.liquido.base.pojo.vo;

import java.time.LocalDate;
import javax.persistence.Convert;
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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PageMerchantMessageVo extends PageCondition {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    @NotNull
    private LocalDate date;

    @NotNull
    private String timeZone;

}
