package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalChargeBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Additional Type
     * such as: "PENALTY_INTEREST"
     */
    @NotNull
    private String type;

    /**
     * Additional Amount
     * unit:cent
     */
    @NotNull
    private BigDecimal amount;

    private String remark;
}
