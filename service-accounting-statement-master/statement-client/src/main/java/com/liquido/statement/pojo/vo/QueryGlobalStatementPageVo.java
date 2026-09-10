package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.GlobalTargetTypeEnum;
import com.liquido.core.mvc.vo.PageCondition;

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
public class QueryGlobalStatementPageVo extends PageCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(value = 1, message = "merchantId invalid")
    private Long merchantId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    @Convert(converter = GlobalTargetTypeEnum.Convert.class)
    private GlobalTargetTypeEnum targetType;

    /**
     * createTime UTC+0
     */
    private LocalDateTime starTime;

    /**
     * createTime UTC+0
     */
    private LocalDateTime endTime;
}
