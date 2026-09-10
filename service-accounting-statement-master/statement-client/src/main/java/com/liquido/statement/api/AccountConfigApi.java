package com.liquido.statement.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.BalanceAlarmConfigDto;
import com.liquido.statement.pojo.vo.AccountConfigVo;
import com.liquido.statement.pojo.vo.BatchAddAccountConfigVo;
import com.liquido.statement.pojo.vo.ListAccountConfigVo;
import com.liquido.statement.pojo.vo.PageAccountConfigVo;
import com.liquido.statement.pojo.vo.QueryAccountConfigVo;
import com.liquido.statement.pojo.vo.UpdateAccountDepositVo;
import com.liquido.statement.pojo.vo.UpdateBalanceAlarmConfigVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountConfigApi {

    @PostMapping("/statement/account-config/batch-add")
    ResponseDto<List<AccountConfigDto>> batchAddAccountConfig(
            @RequestBody @Valid BatchAddAccountConfigVo vo);

    @PostMapping("/statement/account-config/all/query")
    ResponseDto<List<AccountConfigDto>> queryAllAccountConfig();

    @PostMapping("/statement/account-config/list")
    ResponseDto<List<AccountConfigDto>> listAccountConfig(
            @RequestBody @Valid ListAccountConfigVo vo);

    @PostMapping("/statement/account-config/page")
    ResponseDto<PageVo<AccountConfigDto>> pageAccountConfig(
            @RequestBody @Valid PageAccountConfigVo vo);

    @PostMapping("/statement/account-config/info")
    ResponseDto<AccountConfigDto> getEffectiveAccountConfigInfo(
            @RequestBody @Valid QueryAccountConfigVo vo);

    @PostMapping("/statement/account/balance/alarm/config/list")
    ResponseDto<List<BalanceAlarmConfigDto>> queryBalanceAlarmConfig(
            @RequestBody @Valid AccountConfigVo vo);

    @PostMapping("/statement/account/balance/alarm/config/update")
    ResponseDto<Void> updateAccountConfig(@RequestBody @Valid UpdateBalanceAlarmConfigVo vo);

    @PostMapping("/statement/account/deposit/config/update")
    ResponseDto<Void> updateAccountDepositConfig(@RequestBody @Valid UpdateAccountDepositVo vo);

}
