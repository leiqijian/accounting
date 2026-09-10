package com.liquido.statement.pojo.bo;

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
public class CalculateConfigBo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Boolean realTimeRate;

    private Boolean getRateByCreateTime;

}
