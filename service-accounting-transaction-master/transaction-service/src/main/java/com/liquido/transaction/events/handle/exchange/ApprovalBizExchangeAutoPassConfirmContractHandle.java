package com.liquido.transaction.events.handle.exchange;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.dto.ApprovalApplyDto;
import com.liquido.statement.pojo.vo.ApprovalApplyVo;
import com.liquido.transaction.common.properties.LarkProperties;
import com.liquido.transaction.enums.ApprovalBizExchangeStatusEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.enums.ExchangeAccountTypeEnum;
import com.liquido.transaction.exception.TransactionExceptionCode;
import com.liquido.transaction.pojo.bo.ApprovalBizExchangeConfigBo;
import com.liquido.transaction.pojo.bo.ApprovalBizHandleBo;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizExchange;
import com.liquido.transaction.pojo.form.ApprovalBizExchangeForm;
import com.liquido.transaction.pojo.mapper.ModelMapper;
import com.liquido.transaction.pojo.vo.AgreeApprovalInstancesNodeVo;
import com.liquido.transaction.pojo.vo.ReturnApprovalInstancesVo;
import com.liquido.transaction.service.LarkApprovalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApprovalBizExchangeAutoPassConfirmContractHandle
        implements BaseApprovalBizExchangeHandle {

    private final LarkApprovalService larkApprovalService;
    private final StatementApis.StatementFeign statementFeign;
    private final ModelMapper modelMapper;
    private final LarkProperties.ApprovalExchange approvalExchangeProperties;

    @Override
    public boolean isSupport(final Approval approval, final ApprovalBizExchange exchange) {
        return ApprovalStatusEnum.WAIT_PROCESSING == approval.getStatus()
                && ApprovalBizExchangeStatusEnum.WAIT_EXCHANGE_RATE == exchange.getStatus()
                && Boolean.FALSE.equals(exchange.getNeedConfirmContract());
    }

    @Override
    public ApprovalBizHandleBo<ApprovalBizExchangeStatusEnum> handle(
            final Approval approval, final ApprovalBizExchange exchange,
            final ApprovalBizExchangeConfigBo exchangeConfigBo) {

        log.info("approval biz exchange auto-pass confirm contract exchange={}", exchange);
        ApprovalBizHandleBo<ApprovalBizExchangeStatusEnum> bizHandleBo =
                new ApprovalBizHandleBo<>();
        try {
            bizHandleBo = exchange.getExchangeAccountType() != ExchangeAccountTypeEnum.OFFLINE
                    ? ApprovalBizHandleBo.success(ApprovalBizExchangeStatusEnum.PROCESSING)
                    : ApprovalBizHandleBo.success(ApprovalBizExchangeStatusEnum.WAIT_PROOF);

            exchange.setContractConfirmFlag(true);

            Optional.ofNullable(frozenAmountIfNecessary(exchange, bizHandleBo).getTransactionId())
                    .ifPresent(exchange::setTransactionId);

            if (!bizHandleBo.getHandleFlag()) {
                return bizHandleBo;
            }

            // modify lark form the Deduction Amount、Receipt Amount(USD)
            final ApprovalBizExchangeForm form =
                    modelMapper.convertCreateApprovalBizExchangeVo(exchange);

            final Map<String, String> formMap = approvalExchangeProperties.getFormMap().stream()
                    .collect(Collectors.toMap(LarkProperties.ColumnBo::getColumn,
                            LarkProperties.ColumnBo::getLarkColumn));

            larkApprovalService.agreeApprovalInstanceNode(AgreeApprovalInstancesNodeVo.builder()
                    .approvalCode(approvalExchangeProperties.getApprovalCode())
                    .instanceCode(exchange.getApproval().getInstanceCode())
                    .formMap(formMap)
                    .formData(form)
                    .build());

        } catch (Exception e) {
            log.error("approval exchange frozenAmount fail,e:", e);
            larkApprovalService.returnApprovalToLastNode(ReturnApprovalInstancesVo.builder()
                    .instanceCode(exchange.getApproval().getInstanceCode()).reason(e.getMessage())
                    .build());
            bizHandleBo.setSuccessStatus(ApprovalBizExchangeStatusEnum.WAIT_EXCHANGE_RATE);
        }

        return bizHandleBo;
    }

    private ApprovalApplyDto frozenAmountIfNecessary(
            final ApprovalBizExchange exchange,
            final ApprovalBizHandleBo<ApprovalBizExchangeStatusEnum> bizHandleBo) {

        final ApprovalApplyDto approvalApplyDto = new ApprovalApplyDto();
        if (ExchangeAccountTypeEnum.OFFLINE == exchange.getExchangeAccountType()) {
            return approvalApplyDto;
        }
        if (ApprovalBizExchangeStatusEnum.PROCESSING != bizHandleBo.getSuccessStatus()) {
            return approvalApplyDto;
        }

        final ResponseDto<ApprovalApplyDto> responseDto =
                statementFeign.approvalApply(ApprovalApplyVo.builder()
                        .bizTypeCode(BusinessTypeEnum.EXCHANGE)
                        .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                        .merchantId(exchange.getMerchantId())
                        .accountId(exchange.getAccountId())
                        .amount(exchange.getExchangeAmount()).build());

        CheckResponseUtil.checkResponseData(responseDto);

        log.info("apply frozen withdrawal amount transactionId-> {}",
                responseDto.getData().getTransactionId());
        return Optional.ofNullable(responseDto.getData()).stream()
                .filter(result -> Objects.nonNull(result.getTransactionId())).findFirst()
                .orElseThrow(
                        TransactionExceptionCode.APPLY_FROZEN_WITHDRAWAL_AMOUNT_ERROR::exception);
    }

}
