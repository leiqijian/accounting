package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.MessageSourceTypeEnum;
import com.liquido.base.enums.MessageTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMerchantMessageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @NotNull
    @Convert(converter = MessageSourceTypeEnum.Convert.class)
    private MessageSourceTypeEnum messageSourceType;

    @NotNull
    @Convert(converter = MessageTypeEnum.Convert.class)
    private MessageTypeEnum messageType;

    @NotBlank
    @Length(max = 400)
    private String message;
}
