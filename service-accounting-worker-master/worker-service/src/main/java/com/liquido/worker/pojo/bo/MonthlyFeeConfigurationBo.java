package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyFeeConfigurationBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, String> calculationRuleMap;

    private List<MonthlyFeeConfigurationDto> feeConfigList;

}
