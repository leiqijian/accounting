package com.liquido.statement.pojo.dto;

import java.io.Serializable;
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
public class CountryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;

    private String name;

    private String timezone;

    private String timezoneName;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;
}
