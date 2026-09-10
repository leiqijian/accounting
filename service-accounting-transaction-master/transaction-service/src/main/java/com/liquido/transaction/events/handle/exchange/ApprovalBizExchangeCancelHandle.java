package com.liquido.transaction.events.handle.exchange;

import com.liquido.transaction.enums.ApprovalBizExchangeStatusEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
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
public class ApprovalBizExchangeCancelHandle implements BaseApprovalBizExchangeHandle {

    @Override
    public boolean isSupport(final Approval approval, final ApprovalBizExchange exchange) {
        return ApprovalStatusEnum.CANCELED == approval.getStatus();
    }

    @Override
    public ApprovalBizHandleBo<ApprovalBizExchangeStatusEnum> handle(
            final Approval approval,
            final ApprovalBizExchange exchange,
            final ApprovalBizExchangeConfigBo exchangeConfigBo) {

        return ApprovalBizHandleBo.success(ApprovalBizExchangeStatusEnum.CANCELED);
    }

}
