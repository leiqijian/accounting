package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.OwnerEnum;
import com.liquido.base.pojo.vo.ServiceApplyingItemVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class SubMerchantDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    private String merchantCode;

    private String merchantName;

    private OwnerEnum owner;

    private String legalName;

    private String commercialName;

    private String taxId;

    private String incorporationCountry;

    private List<String> website;

    private List<ServiceApplyingItemVo> serviceApplying;

    private String industry;

    private List<CountryCodeEnum> cardsAppicable;

    private String subMerchantId;

    private String merchantRemark;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
