package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.OwnerEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class AddSubMerchantVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    @NotBlank
    private String merchantCode;

    @NotBlank
    private String merchantName;

    private OwnerEnum owner;

    @NotBlank
    private String legalName;

    private String commercialName;

    private String taxId;

    private String incorporationCountry;

    private List<String> website;

    private List<ServiceApplyingItemVo> serviceApplying;

    private String industry;

    private List<CountryCodeEnum> cardsAppicable;

    @NotBlank
    private String subMerchantId;

    private String merchantRemark;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
