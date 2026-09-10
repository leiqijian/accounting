package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.OwnerEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMerchantVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * trading system sync
     * unique
     */
    @NotBlank
    private String code;

    @NotBlank
    private String uuid;

    @NotBlank
    private String name;

    private String logoIcon;

    @Min(0)
    private Integer weight;

    @Min(0)
    private Integer reportWeight;

    @NotNull
    private Boolean innerFlag;

    @NotNull
    @Convert(converter = OwnerEnum.Convert.class)
    private OwnerEnum owner;

    @NotNull
    private Boolean mergerAccount;

}
