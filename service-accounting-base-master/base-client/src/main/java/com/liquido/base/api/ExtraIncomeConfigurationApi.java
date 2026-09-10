package com.liquido.base.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.vo.QueryExtraIncomeConfigurationVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ExtraIncomeConfigurationApi {

    @PostMapping("/base/extra/income/configuration/query")
    ResponseDto<List<ExtraIncomeConfigurationDto>> queryExtraIncomeConfiguration(
            @RequestBody @Valid final QueryExtraIncomeConfigurationVo vo);
}
