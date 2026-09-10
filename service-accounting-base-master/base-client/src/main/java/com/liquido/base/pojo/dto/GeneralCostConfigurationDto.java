package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralCostConfigurationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // General customization cost configuration
    private List<GeneralCostConfigDto> customConfig;

    // General monthly cost configuration
    private List<GeneralCostConfigDto> defaultConfig;

}
