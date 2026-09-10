package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Convert;
import javax.validation.constraints.Min;

import com.liquido.base.enums.OwnerEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListMerchantVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private List<Long> ids;

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
