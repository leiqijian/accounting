package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@SuppressWarnings("PMD.TooManyFields")
public class PageTokenizationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * token Id encrypted with md5
     */
    private String tokenId;

    private LocalDateTime tokenizationCreatedTime;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    private String bin;

    private String brand;

    private String last4Digit;

    private String cardHolderName;

    private BigDecimal expirationYear;

    private BigDecimal expirationMonth;
}
