package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryWorkingDayVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    @NotNull
    @Min(2020)
    private Integer year;

    private Boolean workday;

}
