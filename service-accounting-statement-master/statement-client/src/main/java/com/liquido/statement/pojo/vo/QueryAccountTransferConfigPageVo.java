package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.core.mvc.vo.PageCondition;
import com.liquido.statement.enums.TransferConfigStateEnum;

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
public class QueryAccountTransferConfigPageVo extends PageCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    private CountryCodeEnum countryCode;

    private String merchantCode;

    @Convert(converter = TransferConfigStateEnum.Convert.class)
    private TransferConfigStateEnum state;

}
