package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.api.AccountConfigApi;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.BalanceAlarmConfigDto;
import com.liquido.statement.pojo.vo.AccountConfigVo;
import com.liquido.statement.pojo.vo.BatchAddAccountConfigVo;
import com.liquido.statement.pojo.vo.ListAccountConfigVo;
import com.liquido.statement.pojo.vo.PageAccountConfigVo;
import com.liquido.statement.pojo.vo.QueryAccountConfigVo;
import com.liquido.statement.pojo.vo.UpdateAccountDepositVo;
import com.liquido.statement.pojo.vo.UpdateBalanceAlarmConfigVo;
import com.liquido.statement.service.AccountConfigService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class AccountConfigController implements AccountConfigApi {

    private final AccountConfigService accountConfigService;

    @Override
    @PostMapping("/statement/account-config/batch-add")
    public ResponseDto<List<AccountConfigDto>> batchAddAccountConfig(
            @RequestBody @Valid final BatchAddAccountConfigVo vo) {
        return ResponseDto.success(accountConfigService.batchAddAccountConfig(vo));
    }

    @Override
    @PostMapping("/statement/account-config/all/query")
    public ResponseDto<List<AccountConfigDto>> queryAllAccountConfig() {
        return ResponseDto.success(accountConfigService.queryAllAccountConfig());
    }

    @Override
    @PostMapping("/statement/account-config/list")
    public ResponseDto<List<AccountConfigDto>> listAccountConfig(
            @RequestBody @Valid final ListAccountConfigVo vo) {
        return ResponseDto.success(accountConfigService.listAccountConfig(vo));
    }

    @Override
    @PostMapping("/statement/account-config/page")
    public ResponseDto<PageVo<AccountConfigDto>> pageAccountConfig(
            @RequestBody @Valid final PageAccountConfigVo vo) {
        return ResponseDto.success(accountConfigService.pageAccountConfig(vo));
    }

    @Override
    @PostMapping("/statement/account-config/info")
    public ResponseDto<AccountConfigDto> getEffectiveAccountConfigInfo(
            @RequestBody @Valid final QueryAccountConfigVo vo) {
        return ResponseDto.success(accountConfigService.getEffectiveAccountConfigInfo(vo));
    }

    @Override
    @PostMapping("/statement/account/balance/alarm/config/list")
    public ResponseDto<List<BalanceAlarmConfigDto>> queryBalanceAlarmConfig(
            @RequestBody @Valid final AccountConfigVo vo) {
        return ResponseDto.success(accountConfigService.queryBalanceAlarmConfig(vo));
    }

    @Override
    @PostMapping("/statement/account/balance/alarm/config/update")
    public ResponseDto<Void> updateAccountConfig(
            @RequestBody @Valid final UpdateBalanceAlarmConfigVo vo) {
        accountConfigService.updateAccountBalanceAlarmConfig(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/deposit/config/update")
    public ResponseDto<Void> updateAccountDepositConfig(
            @RequestBody @Valid final UpdateAccountDepositVo vo) {
        accountConfigService.updateAccountDepositConfig(vo);
        return ResponseDto.success();
    }

}
