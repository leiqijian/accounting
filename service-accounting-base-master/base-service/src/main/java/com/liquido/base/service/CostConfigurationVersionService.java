package com.liquido.base.service;

import com.liquido.base.enums.CostTypeEnum;

public interface CostConfigurationVersionService {

    int queryActiveConfig(final CostTypeEnum costType);

}
