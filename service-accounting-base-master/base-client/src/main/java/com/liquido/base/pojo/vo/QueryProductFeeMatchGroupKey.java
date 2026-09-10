package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.MonthlyVolumeTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryProductFeeMatchGroupKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer accountFeeVersion;

    /**
     * MonthlyVolumeTypeEnum: 0-amount/1-counts
     */
    @Convert(converter = MonthlyVolumeTypeEnum.Convert.class)
    private MonthlyVolumeTypeEnum monthlyVolumeType;

    private BigDecimal monthlyVolume;

}
