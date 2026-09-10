package com.liquido.statement.service;

import java.util.List;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.AccountTransferConfigDto;
import com.liquido.statement.pojo.entity.AccountTransferConfig;
import com.liquido.statement.pojo.vo.ModifyAccountTransferConfigStateVo;
import com.liquido.statement.pojo.vo.QueryAccountTransferConfigPageVo;
import com.liquido.statement.pojo.vo.SaveAccountTransferConfigVo;

public interface AccountTransferConfigService {

    void saveAccountAutoTransferConfig(final SaveAccountTransferConfigVo vo);

    void updateAccountAutoTransferConfigState(final ModifyAccountTransferConfigStateVo vo);

    AccountTransferConfig findAccountTransferConfig(final Long merchantId,
                                                    final Long payinAccountId,
                                                    final Long payoutAccountId);

    AccountTransferConfig findByMerchantIdAndPayinAccountId(final Long merchantId,
                                                            final Long payinAccountId);

    AccountTransferConfig findByMerchantIdAndPayoutAccountId(final Long merchantId,
                                                             final Long payoutAccountId);

    List<AccountTransferConfig> queryCustomizeTimerConfig();

    PageVo<AccountTransferConfigDto> queryAccountAutoTransferConfigPage(
            final QueryAccountTransferConfigPageVo vo);
}
