package com.liquido.worker.pojo.dto.tokenization;

import java.io.Serializable;
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
public class DwSyncTokenInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;

    private DwSyncPayerDto payer;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    private DwSyncDisplayedCardInfoDto displayedCardInfo;
}
