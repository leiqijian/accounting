package com.liquido.statement.service;

import com.liquido.statement.pojo.dto.GlobalAccountDto;
import com.liquido.statement.pojo.dto.GlobalAccountInfoDto;
import com.liquido.statement.pojo.entity.GlobalAccount;
import com.liquido.statement.pojo.vo.GlobalAccountTopupVo;
import com.liquido.statement.pojo.vo.GlobalAccountTransferOutVo;

public interface GlobalAccountService {

    GlobalAccount findGlobalAccount(final Long merchantId);

    GlobalAccountDto queryGlobalAccount(final Long merchantId);

    GlobalAccountInfoDto queryGlobalAccountInfo(final Long merchantId);

    void topupGlobalAccount(final GlobalAccountTopupVo vo);

    boolean transferOutGlobalAccount(final GlobalAccountTransferOutVo vo);

    void autoRechargeToSubAccount(final Long subAccountId);

    void tryLockGlobalAccount(final Long globalAccountId, final String lockVal);

    void unLockGlobalAccount(final Long globalAccountId, final String lockVal);

}
