package com.liquido.base.pojo.dto;

import java.io.Serializable;
import javax.persistence.Convert;

import com.liquido.base.enums.CreditCardGroupCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardGroupDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String groupName;

    @Convert(converter = CreditCardGroupCodeEnum.Convert.class)
    private CreditCardGroupCodeEnum groupCode;

    private Integer iinBegin;

    private Integer iinEnd;
}
