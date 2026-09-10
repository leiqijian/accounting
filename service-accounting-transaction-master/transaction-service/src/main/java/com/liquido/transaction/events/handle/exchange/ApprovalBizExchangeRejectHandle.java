package com.liquido.transaction.events.handle.exchange;

import java.util.Optional;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.vo.ApprovalRollBackVo;
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
public class ApprovalBizExchangeRejectHandle implements BaseApprovalBizExchangeHandle {

    private final StatementApis.StatementFeign statementFeign;

    @Override
    public boolean isSupport(final Approval approval, final ApprovalBizExchange exchange) {
        return ApprovalStatusEnum.REJECTED == approval.getStatus()
                && ApprovalBizExchangeStatusEnum.REJECTED != exchange.getStatus()
                && canRollBackBiz(exchange);
    }

    private boolean canRollBackBiz(final ApprovalBizExchange exchange) {

        return (ExchangeAccountTypeEnum.PAY_IN == exchange.getExchangeAccountType()
                || ExchangeAccountTypeEnum.PAY_OUT == exchange.getExchangeAccountType())
                && exchange.getStatus() == ApprovalBizExchangeStatusEnum.PROCESSING;

    }

    @Override
    public ApprovalBizHandleBo<ApprovalBizExchangeStatusEnum> handle(
            final Approval approval,
            final ApprovalBizExchange exchange,
            final ApprovalBizExchangeConfigBo exchangeConfigBo) {

        boolean executeResult = false;
        String message;
        log.info("approval biz exchange online rollback request statement exchange={}", exchange);
        try {
            final ResponseDto<Void> responseDto = statementFeign.approvalRollBack(
                    ApprovalRollBackVo.builder()
                            .bizTypeCode(BusinessTypeEnum.EXCHANGE)
                            .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                            .transactionId(Optional.ofNullable(exchange.getTransactionId())
                                    .orElse(exchange.getId()))
                            .merchantId(exchange.getMerchantId())
                            .accountId(exchange.getAccountId())
                            .amount(exchange.getExchangeAmount())
                            .build());
            executeResult = responseDto.isSuccess();
            message = responseDto.getMsg();

            if (!executeResult) {
                log.error("approval exchange reject roll back errormessage:{},exchangeId:{}",
                        message, exchange.getId());
            }
        } catch (Exception e) {
            log.error("approval transferOut reject roll back fail:", e);
            message = e.getMessage();
        }

        return executeResult
                ? ApprovalBizHandleBo.success(ApprovalBizExchangeStatusEnum.REJECTED)
                : ApprovalBizHandleBo.error(ApprovalBizExchangeStatusEnum.FAILED, message);
    }
}
