package com.liquido.worker.pojo.vo;

import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.core.mvc.vo.PageCondition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageTokenizationVo extends PageCondition {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String merchantCode;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * tokenization unique id
     */
    private String tokenId;

    private String tokenMd5;

    private String cardHolderName;

    private String last4Digit;

    private String bin;

    /**
     * start date of the UTC(Submit Time)
     */
    private LocalDateTime startDate;

    /**
     * end date of the UTC(Submit Time)
     */
    private LocalDateTime endDate;

}
