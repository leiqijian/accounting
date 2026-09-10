package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Convert;
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
public class InitExchangeManualVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDateTime exchangeTime;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    private List<Long> accountIdList;

}
