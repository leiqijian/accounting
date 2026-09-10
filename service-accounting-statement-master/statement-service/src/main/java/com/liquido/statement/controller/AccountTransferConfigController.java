package com.liquido.statement.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.api.AccountTransferConfigApi;
import com.liquido.statement.pojo.dto.AccountTransferConfigDto;
import com.liquido.statement.pojo.vo.ModifyAccountTransferConfigStateVo;
import com.liquido.statement.pojo.vo.QueryAccountTransferConfigPageVo;
import com.liquido.statement.pojo.vo.SaveAccountTransferConfigVo;
import com.liquido.statement.service.AccountTransferConfigService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountTransferConfigController implements AccountTransferConfigApi {

    private final AccountTransferConfigService accountTransferConfigService;

    @Override
    @PostMapping("/statement/account/internal/auto/transfer/config/save")
    public ResponseDto<Void> saveAccountAutoTransferConfig(
            @RequestBody @Valid final SaveAccountTransferConfigVo vo) {
        accountTransferConfigService.saveAccountAutoTransferConfig(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/internal/auto/transfer/config/page")
    public ResponseDto<PageVo<AccountTransferConfigDto>> queryAccountAutoTransferConfigPage(
            @RequestBody @Valid final QueryAccountTransferConfigPageVo vo) {
        return ResponseDto.success(
                accountTransferConfigService.queryAccountAutoTransferConfigPage(vo));
    }

    @Override
    @PostMapping("/statement/account/internal/auto/transfer/config/state/switch")
    public ResponseDto<Void> updateAccountAutoTransferConfigState(
            @RequestBody @Valid final ModifyAccountTransferConfigStateVo vo) {
        accountTransferConfigService.updateAccountAutoTransferConfigState(vo);
        return ResponseDto.success();
    }

}
