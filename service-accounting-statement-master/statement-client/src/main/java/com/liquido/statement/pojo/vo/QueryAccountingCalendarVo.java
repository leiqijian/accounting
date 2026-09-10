package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAccountingCalendarVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(1)
    private Long merchantId;

    @NotNull
    @Min(1)
    private Long accountId;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
