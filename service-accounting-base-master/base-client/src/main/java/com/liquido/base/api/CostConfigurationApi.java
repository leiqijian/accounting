package com.liquido.base.api;

import javax.validation.Valid;

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
import com.liquido.base.pojo.vo.QueryCostApmConfigVo;
import com.liquido.base.pojo.vo.QueryCostCardConfigVo;
import com.liquido.base.pojo.vo.QueryCostConfigurationByIdVo;
import com.liquido.base.pojo.vo.QueryCostConfigurationVo;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface CostConfigurationApi {

    @PostMapping("/base/cost/apm/config/list")
    ResponseDto<ApmCostConfigurationDto> queryApmCostConfig(
            @RequestBody @Valid final QueryApmCostConfigVo vo);

    @PostMapping("/base/cost/card/config/list")
    ResponseDto<CardCostConfigurationDto> queryCardCostConfig();

    @PostMapping("/base/cost/config/list")
    ResponseDto<PageVo<CostConfigDto>> queryCostConfig(
            @RequestBody @Valid final QueryCostConfigurationVo vo);

    @PostMapping("/base/cost/config/query")
    ResponseDto<CostConfigDto> queryCostConfigById(
            @RequestBody @Valid final QueryCostConfigurationByIdVo vo);

    @PostMapping("/base/cost/config/delete")
    ResponseDto<Void> deleteCostConfigById(
            @RequestBody @Valid final DeleteCostConfigurationVo vo);

    @PostMapping("/base/cost/config/add")
    ResponseDto<Void> addOrUpdateCostConfig(
            @RequestBody @Valid final CreateCostConfigurationVo vo);

    @PostMapping("/base/cost/config/update")
    ResponseDto<Void> updateCostConfig(
            @RequestBody @Valid final CostConfigurationVo vo);

    @PostMapping("/base/cost/config/init/query")
    ResponseDto<CostConfigInitDto> queryCostConfigInitList();

    @PostMapping("/base/cost/apm/config/init/query")
    ResponseDto<CostApmConfigInitDto> initCostApmConfigList();

    @PostMapping("/base/cost/apm/config/query")
    ResponseDto<CostApmConfigurationDto> costApmConfigList(
            @RequestBody @Valid final QueryCostApmConfigVo vo);

    @PostMapping("/base/cost/card/config/init/query")
    ResponseDto<CostCardConfigInitDto> initCostCardConfigList();

    @PostMapping("/base/cost/card/config/query")
    ResponseDto<CostCardConfigurationDto> costCardConfigList(
            @RequestBody @Valid final QueryCostCardConfigVo vo);
}
