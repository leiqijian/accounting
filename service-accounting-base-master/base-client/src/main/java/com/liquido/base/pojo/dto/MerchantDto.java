package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Convert;

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
public class MerchantDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String code;

    private String uuid;

    private String name;

    private String industry;

    private String logoIcon;

    private Integer weight;

    private Integer reportWeight;

    private Boolean innerFlag;

    @Convert(converter = OwnerEnum.Convert.class)
    private OwnerEnum owner;

    private Boolean mergerAccount;

    private Boolean subMerchantDividedBill;

    private Boolean subMerchantExist;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Long createdBy;

    private Long updatedBy;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private List<CountryCodeEnum> notSupportMergerCountry;

}
