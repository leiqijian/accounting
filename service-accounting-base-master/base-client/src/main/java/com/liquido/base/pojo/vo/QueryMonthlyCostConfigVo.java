package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryMonthlyCostConfigVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Integer activeMonth;
}
