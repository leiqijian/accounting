package com.liquido.base.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.api.MonthlyFeeConfigurationApi;
import com.liquido.base.pojo.dto.MonthFxLoseConfigDto;
import com.liquido.base.pojo.dto.MonthlyAccountFeeConfigDto;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.vo.BatchAddMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.BatchQueryProductMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.ListAccountMonthFxLoseVo;
import com.liquido.base.pojo.vo.QueryAccountMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.QueryMonthFeeConfigVo;
import com.liquido.base.pojo.vo.QueryProductMonthlyFeeConfigVo;
import com.liquido.base.service.MonthlyFeeConfigurationService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MonthlyFeeConfigurationController implements MonthlyFeeConfigurationApi {

    private final MonthlyFeeConfigurationService monthlyFeeConfigurationService;

    @Override
    @PostMapping("/base/monthly-fee-configuration/batch-add")
    public ResponseDto<List<MonthlyFeeConfigurationDto>> batchAddMonthlyFeeConfig(
            @Valid @RequestBody final BatchAddMonthlyFeeConfigVo vo) {
        return ResponseDto.success(monthlyFeeConfigurationService.saveAll(vo.getListVo()));
    }

    @Override
    @PostMapping("/base/product/monthly-fee-config/query")
    public ResponseDto<List<MonthlyFeeConfigurationDto>> queryProductMonthlyFeeConfigInfo(
            @Valid @RequestBody final QueryProductMonthlyFeeConfigVo vo) {
        return ResponseDto.success(
                monthlyFeeConfigurationService.queryProductMonthFeeConfigInfo(vo));
    }

    @Override
    @PostMapping("/base/product/monthly-fee-config/batch-query")
    public ResponseDto<List<MonthlyFeeConfigurationDto>> batchQueryProductMonthlyFeeConfigInfo(
            @Valid @RequestBody final BatchQueryProductMonthlyFeeConfigVo batchVo) {
        return ResponseDto.success(monthlyFeeConfigurationService
                .batchQueryProductMonthlyFeeConfigInfo(batchVo));
    }


    /**
     * Query account monthly fee configuration(Excluding 'FX_ LOSE')
     */
    @Override
    @PostMapping("/base/account/monthly-fee-config/query")
    public ResponseDto<MonthlyAccountFeeConfigDto> queryAccountMonthFeeConfig(
            @Valid @RequestBody final QueryAccountMonthlyFeeConfigVo vo) {
        final MonthlyAccountFeeConfigDto dto =
                monthlyFeeConfigurationService.queryAccountMonthFeeConfigExclFxLose(vo);
        return ResponseDto.success(dto);
    }

    @Override
    @PostMapping("/base/account/monthly-fx-config/list")
    public ResponseDto<List<MonthFxLoseConfigDto>> listAllAccountMonthFxLose(
            @Valid @RequestBody final ListAccountMonthFxLoseVo vo) {
        final List<MonthFxLoseConfigDto> dtoList =
                monthlyFeeConfigurationService.listAllAccountMonthFxLose(vo);
        return ResponseDto.success(dtoList);
    }

    @Override
    @PostMapping("/base/monthly-fee-config/list")
    public ResponseDto<List<MonthlyFeeConfigurationDto>> listMonthFeeConfig(
            @Valid @RequestBody final QueryMonthFeeConfigVo vo) {
        final List<MonthlyFeeConfigurationDto> dto =
                monthlyFeeConfigurationService.listMonthlyFeeConfigInfo(vo);
        return ResponseDto.success(dto);
    }

    @Override
    @PostMapping("/base/monthly-fee-config/query")
    public ResponseDto<List<MonthlyFeeConfigurationDto>> queryByIds(
            @RequestBody final List<Long> ids) {
        final List<MonthlyFeeConfigurationDto> dto =
                monthlyFeeConfigurationService.queryMonthlyFeeConfigurationByIds(ids);
        return ResponseDto.success(dto);
    }

}
