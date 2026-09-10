package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryCostConfigurationByIdVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(0)
    private Long id;

}
