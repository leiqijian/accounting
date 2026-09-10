package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.MonthlyVolumeTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryProductFeeMatchVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * fk
     */
    @NotNull
    private List<Long> accountProductIds;

    @NotNull
    private Integer accountFeeVersion;

    /**
     * MonthlyVolumeTypeEnum: 0-amount/1-counts
     */
    @NotNull
    @Convert(converter = MonthlyVolumeTypeEnum.Convert.class)
    private MonthlyVolumeTypeEnum monthlyVolumeType;

    @NotNull
    private BigDecimal monthlyVolume;

}
