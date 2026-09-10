package com.liquido.transaction.events.handle.exchange;

import com.liquido.transaction.enums.ApprovalBizExchangeStatusEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.enums.ExchangeAccountTypeEnum;
import com.liquido.transaction.pojo.bo.ApprovalBizExchangeConfigBo;
import com.liquido.transaction.pojo.bo.ApprovalBizHandleBo;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApprovalBizExchangeOfflineWaitProofHandle
        implements BaseApprovalBizExchangeHandle {

    @Override
    public boolean isSupport(final Approval approval, final ApprovalBizExchange exchange) {
        return ApprovalStatusEnum.PROCESSING == approval.getStatus()
                && ApprovalBizExchangeStatusEnum.WAIT_CONFIRM_CONTRACT == exchange.getStatus()
                && Boolean.TRUE.equals(exchange.getContractConfirmFlag())
                && ExchangeAccountTypeEnum.OFFLINE == exchange.getExchangeAccountType();
    }

    @Override
    public ApprovalBizHandleBo<ApprovalBizExchangeStatusEnum> handle(
            final Approval approval,
            final ApprovalBizExchange exchange,
            final ApprovalBizExchangeConfigBo exchangeConfigBo) {

        return ApprovalBizHandleBo.success(ApprovalBizExchangeStatusEnum.WAIT_PROOF);
    }
}
