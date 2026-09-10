package com.liquido.base.pojo.vo;


import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class EditMonthlyFeeConfigurationVo extends MonthlyFeeConfigurationVo
        implements Serializable {
    private static final long serialVersionUID = 1L;

}
