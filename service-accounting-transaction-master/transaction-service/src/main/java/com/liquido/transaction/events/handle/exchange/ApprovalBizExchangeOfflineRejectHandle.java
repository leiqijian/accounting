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
public class ApprovalBizExchangeOfflineRejectHandle implements BaseApprovalBizExchangeHandle {


    @Override
    public boolean isSupport(final Approval approval, final ApprovalBizExchange exchange) {
        return ApprovalStatusEnum.REJECTED == approval.getStatus()
                && ApprovalBizExchangeStatusEnum.REJECTED != exchange.getStatus()
                && buildConditions(exchange);
    }


    public boolean buildConditions(final ApprovalBizExchange exchange) {
        return ExchangeAccountTypeEnum.OFFLINE == exchange.getExchangeAccountType()
                || ((ExchangeAccountTypeEnum.PAY_IN == exchange.getExchangeAccountType()
                || ExchangeAccountTypeEnum.PAY_OUT == exchange.getExchangeAccountType())
                && ApprovalBizExchangeStatusEnum.WAIT_EXCHANGE_RATE == exchange.getStatus());
    }

    @Override
    public ApprovalBizHandleBo<ApprovalBizExchangeStatusEnum> handle(
            final Approval approval,
            final ApprovalBizExchange exchange,
            final ApprovalBizExchangeConfigBo exchangeConfigBo) {

        return ApprovalBizHandleBo.success(ApprovalBizExchangeStatusEnum.REJECTED);
    }
}
