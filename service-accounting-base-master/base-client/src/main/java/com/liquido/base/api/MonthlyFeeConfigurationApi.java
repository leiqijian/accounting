package com.liquido.base.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.MonthFxLoseConfigDto;
import com.liquido.base.pojo.dto.MonthlyAccountFeeConfigDto;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.vo.BatchAddMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.BatchQueryProductMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.ListAccountMonthFxLoseVo;
import com.liquido.base.pojo.vo.QueryAccountMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.QueryMonthFeeConfigVo;
import com.liquido.base.pojo.vo.QueryProductMonthlyFeeConfigVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MonthlyFeeConfigurationApi {

    @PostMapping("/base/monthly-fee-configuration/batch-add")
    ResponseDto<List<MonthlyFeeConfigurationDto>> batchAddMonthlyFeeConfig(
            @Valid @RequestBody BatchAddMonthlyFeeConfigVo vo);

    @PostMapping("/base/product/monthly-fee-config/query")
    ResponseDto<List<MonthlyFeeConfigurationDto>> queryProductMonthlyFeeConfigInfo(
            @Valid @RequestBody QueryProductMonthlyFeeConfigVo vo);


    @PostMapping("/base/product/monthly-fee-config/batch-query")
    ResponseDto<List<MonthlyFeeConfigurationDto>> batchQueryProductMonthlyFeeConfigInfo(
            @Valid @RequestBody BatchQueryProductMonthlyFeeConfigVo vo);

    /**
     * query all monthly fee config by accountId and date
     */
    @PostMapping("/base/account/monthly-fee-config/query")
    ResponseDto<MonthlyAccountFeeConfigDto> queryAccountMonthFeeConfig(
            @Valid @RequestBody QueryAccountMonthlyFeeConfigVo vo);

    @PostMapping("/base/account/monthly-fx-config/list")
    ResponseDto<List<MonthFxLoseConfigDto>> listAllAccountMonthFxLose(
            @Valid @RequestBody final ListAccountMonthFxLoseVo vo);

    @PostMapping("/base/monthly-fee-config/list")
    ResponseDto<List<MonthlyFeeConfigurationDto>> listMonthFeeConfig(
            @Valid @RequestBody final QueryMonthFeeConfigVo vo);

    @PostMapping("/base/monthly-fee-config/query")
    ResponseDto<List<MonthlyFeeConfigurationDto>> queryByIds(
            @RequestBody final List<Long> ids);

}
