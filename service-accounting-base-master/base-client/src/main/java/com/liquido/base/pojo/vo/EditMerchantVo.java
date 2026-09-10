package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.OwnerEnum;

import lombok.Data;

@Data
public class EditMerchantVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long id;

    /**
     * trading system sync
     */
    private String code;

    private String uuid;

    private String name;

    private String logoIcon;

    @Min(-1)
    private Integer weight;

    @Min(-1)
    private Integer reportWeight;

    private Boolean innerFlag;

    @Convert(converter = OwnerEnum.Convert.class)
    private OwnerEnum owner;

    private Boolean mergerAccount;

}
