package com.liquido.base.service;

import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigurationDto;
import com.liquido.base.pojo.dto.CostApmConfigInitDto;
import com.liquido.base.pojo.dto.CostApmConfigurationDto;
import com.liquido.base.pojo.dto.CostCardConfigInitDto;
import com.liquido.base.pojo.dto.CostCardConfigurationDto;
import com.liquido.base.pojo.dto.CostConfigDto;
import com.liquido.base.pojo.dto.CostConfigInitDto;
import com.liquido.base.pojo.vo.CostConfigurationVo;
import com.liquido.base.pojo.vo.CreateCostConfigurationVo;
import com.liquido.base.pojo.vo.DeleteCostConfigurationVo;
import com.liquido.base.pojo.vo.QueryApmCostConfigVo;
import com.liquido.base.pojo.vo.QueryCardCostConfigVo;
import com.liquido.base.pojo.vo.QueryCostApmConfigVo;
import com.liquido.base.pojo.vo.QueryCostCardConfigVo;
import com.liquido.base.pojo.vo.QueryCostConfigurationByIdVo;
import com.liquido.base.pojo.vo.QueryCostConfigurationVo;
import com.liquido.core.mvc.vo.PageVo;

public interface CostConfigurationService {

    ApmCostConfigurationDto queryApmCostConfig(final QueryApmCostConfigVo vo);

    CardCostConfigurationDto queryCardCostConfig();

    CardCostConfigurationDto queryCardCostConfigByVersion(final QueryCardCostConfigVo vo);

    PageVo<CostConfigDto> queryCostConfig(final QueryCostConfigurationVo vo);

    void deleteCostConfig(final DeleteCostConfigurationVo vo);

    void addOrUpdateCostConfig(final CreateCostConfigurationVo vo);

    void updateCostConfig(final CostConfigurationVo vo);

    CostConfigDto queryCostConfigById(final QueryCostConfigurationByIdVo vo);

    CostConfigInitDto queryCostConfigInitList();

    CostApmConfigInitDto initCostApmConfigList();

    CostCardConfigInitDto initCostCardConfigList();

    CostApmConfigurationDto costApmConfigList(final QueryCostApmConfigVo vo);

    CostCardConfigurationDto costCardConfigList(final QueryCostCardConfigVo vo);
}
