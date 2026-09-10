package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class DeleteCostConfigurationVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(0)
    private Long id;

    @NotNull
    @Min(0)
    private Long accountId;
}
