package com.liquido.statement.pojo.vo;

import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAccountBySubAccountVo {

    private Long merchantId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

}
