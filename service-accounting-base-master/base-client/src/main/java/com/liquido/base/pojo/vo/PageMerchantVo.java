package com.liquido.base.pojo.vo;

import javax.persistence.Convert;
import javax.validation.constraints.Min;

import com.liquido.base.enums.OwnerEnum;
import com.liquido.core.mvc.vo.PageCondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PageMerchantVo extends PageCondition {

    private static final long serialVersionUID = 4515311306826269841L;

    private Long id;

    private String code;

    private String uuid;

    private String name;

    private Boolean mergerAccount;

    @Min(-1)
    private Integer weight;

    @Min(-1)
    private Integer reportWeight;

    private Boolean innerFlag;

    @Convert(converter = OwnerEnum.Convert.class)
    private OwnerEnum owner;


}
