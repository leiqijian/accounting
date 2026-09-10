package com.liquido.base.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.api.ExtraIncomeConfigurationApi;
import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.vo.QueryExtraIncomeConfigurationVo;
import com.liquido.base.service.ExtraIncomeConfigurationService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExtraIncomeConfigurationController implements ExtraIncomeConfigurationApi {

    private final ExtraIncomeConfigurationService extraIncomeConfigurationService;

    @PostMapping("/base/extra/income/configuration/query")
    public ResponseDto<List<ExtraIncomeConfigurationDto>> queryExtraIncomeConfiguration(
            @RequestBody @Valid final QueryExtraIncomeConfigurationVo vo) {
        return ResponseDto.success(
                extraIncomeConfigurationService.queryExtraIncomeConfiguration(vo));
    }
}
