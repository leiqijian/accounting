package com.liquido.statement.service;

import java.util.List;

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

public interface AccountConfigService {

    List<AccountConfigDto> batchAddAccountConfig(final BatchAddAccountConfigVo vo);

    List<AccountConfigDto> queryAllAccountConfig();

    List<AccountConfigDto> listAccountConfig(final ListAccountConfigVo vo);

    PageVo<AccountConfigDto> pageAccountConfig(final PageAccountConfigVo vo);

    AccountConfigDto getEffectiveAccountConfigInfo(final QueryAccountConfigVo vo);

    List<BalanceAlarmConfigDto> queryBalanceAlarmConfig(final AccountConfigVo vo);

    void updateAccountBalanceAlarmConfig(final UpdateBalanceAlarmConfigVo vo);

    void updateAccountDepositConfig(final UpdateAccountDepositVo vo);

}
