package com.liquido.base.controller;

import javax.validation.Valid;

import com.liquido.base.api.CostConfigurationApi;
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
import com.liquido.base.service.CostConfigurationService;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
public class CostConfigurationController implements CostConfigurationApi {
    private final CostConfigurationService costConfigurationService;

    @Override
    @PostMapping("/base/cost/apm/config/list")
    public ResponseDto<ApmCostConfigurationDto> queryApmCostConfig(
            @RequestBody @Valid final QueryApmCostConfigVo vo) {
        return ResponseDto.success(costConfigurationService.queryApmCostConfig(vo));
    }

    /**
     * query card(credit-card or debit-card) cost config by accountId and activeMonth
     */
    @Override
    @PostMapping("/base/cost/card/config/list")
    public ResponseDto<CardCostConfigurationDto> queryCardCostConfig() {
        return ResponseDto.success(costConfigurationService.queryCardCostConfig());
    }

    /**
     * query config
     */
    @Override
    @PostMapping("/base/cost/config/list")
    public ResponseDto<PageVo<CostConfigDto>> queryCostConfig(
            @RequestBody @Valid final QueryCostConfigurationVo vo) {
        return ResponseDto.success(costConfigurationService.queryCostConfig(vo));
    }

    /**
     * query config by id
     */
    @Override
    @PostMapping("/base/cost/config/query")
    public ResponseDto<CostConfigDto> queryCostConfigById(
            @RequestBody @Valid final QueryCostConfigurationByIdVo vo) {
        return ResponseDto.success(costConfigurationService.queryCostConfigById(vo));
    }

    /**
     * delete cost config
     */
    @Override
    @PostMapping("/base/cost/config/delete")
    public ResponseDto<Void> deleteCostConfigById(
            @RequestBody @Valid final DeleteCostConfigurationVo vo) {
        costConfigurationService.deleteCostConfig(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/base/cost/config/add")
    public ResponseDto<Void> addOrUpdateCostConfig(
            @RequestBody @Valid final CreateCostConfigurationVo vo) {
        costConfigurationService.addOrUpdateCostConfig(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/base/cost/config/update")
    public ResponseDto<Void> updateCostConfig(
            @RequestBody @Valid final CostConfigurationVo vo) {
        costConfigurationService.updateCostConfig(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/base/cost/config/init/query")
    public ResponseDto<CostConfigInitDto> queryCostConfigInitList() {
        return ResponseDto.success(costConfigurationService.queryCostConfigInitList());
    }

    @Override
    @PostMapping("/base/cost/apm/config/init/query")
    public ResponseDto<CostApmConfigInitDto> initCostApmConfigList() {
        return ResponseDto.success(costConfigurationService.initCostApmConfigList());
    }

    @Override
    @PostMapping("/base/cost/apm/config/query")
    public ResponseDto<CostApmConfigurationDto> costApmConfigList(
            @RequestBody @Valid final QueryCostApmConfigVo vo) {
        return ResponseDto.success(costConfigurationService.costApmConfigList(vo));
    }

    @Override
    @PostMapping("/base/cost/card/config/init/query")
    public ResponseDto<CostCardConfigInitDto> initCostCardConfigList() {
        return ResponseDto.success(costConfigurationService.initCostCardConfigList());
    }

    @Override
    @PostMapping("/base/cost/card/config/query")
    public ResponseDto<CostCardConfigurationDto> costCardConfigList(
            @RequestBody @Valid final QueryCostCardConfigVo vo) {
        return ResponseDto.success(costConfigurationService.costCardConfigList(vo));
    }

}
