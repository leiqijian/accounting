package com.liquido.transaction.events.handle.batchwithdrawal;

import java.math.BigDecimal;
import java.util.List;

import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.dto.DealershipWithdrawalInfoDto;
import com.liquido.statement.pojo.vo.BatchWithdrawalApprovedHandleDetailVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApprovedHandleVo;
import com.liquido.statement.pojo.vo.ListBatchWithdrawalApplyVo;
import com.liquido.transaction.enums.ApprovalBizBatchWithdrawalStatusEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.pojo.bo.ApprovalBizHandleBo;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizBatchWithdrawal;
import com.liquido.transaction.pojo.mapper.ModelMapper;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalBizBatchWithdrawalRejectHandle
        implements BaseApprovalBizBatchWithdrawalHandle {

    private final ModelMapper modelMapper;
    private final StatementApis.StatementFeign statementFeign;

    @Override
    public boolean isSupport(final Approval approval,
                             final ApprovalBizBatchWithdrawal batchWithdrawal) {
        return ApprovalStatusEnum.REJECTED == approval.getStatus()
                && ApprovalBizBatchWithdrawalStatusEnum.REJECTED != batchWithdrawal.getStatus();
    }

    @Override
    public ApprovalBizHandleBo<ApprovalBizBatchWithdrawalStatusEnum> handle(
            final Approval approval, final ApprovalBizBatchWithdrawal batchWithdrawal) {
        boolean executeResult = false;
        String message;
        try {
            log.info("batch withdrawal reject request statement transferOut={}", batchWithdrawal);

            List<DealershipWithdrawalInfoDto> dtoList = statementFeign.listWithdrawalApply(
                    ListBatchWithdrawalApplyVo.builder()
                            .batchId(batchWithdrawal.getBatchId()).build()).getData();

            final List<BatchWithdrawalApprovedHandleDetailVo> rejectedList = Lists.newArrayList();

            BigDecimal unfrozenAmount = BigDecimal.ZERO;

            for (final DealershipWithdrawalInfoDto dto : dtoList) {
                unfrozenAmount = unfrozenAmount.add(dto.getWithdrawalAmount());
                dto.setState(3);
                rejectedList.add(modelMapper.convertBatchWithdrawalApprovedHandleDetailVo(dto));
            }

            final ResponseDto<Void> responseDto = statementFeign.batchWithdrawalApprovedHandle(
                    BatchWithdrawalApprovedHandleVo.builder()
                            .accountId(batchWithdrawal.getAccountId())
                            .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                            .batchId(batchWithdrawal.getBatchId())
                            .unFrozenAmount(unfrozenAmount)
                            .settlementCurrency(batchWithdrawal.getSettlementCurrency())
                            .approvalApprovedAmount(BigDecimal.ZERO)
                            .approvalRejectedAmount(unfrozenAmount)
                            .approvalApprovedCount(0)
                            .approvalApprovedList(Lists.newArrayList())
                            .approvalRejectedList(rejectedList)
                            .build());

            executeResult = responseDto.isSuccess();
            message = responseDto.getMsg();
        } catch (Exception e) {
            log.error("approval transferOut reject roll back fail:", e);
            message = e.getMessage();
        }
        if (executeResult) {
            return ApprovalBizHandleBo.success(ApprovalBizBatchWithdrawalStatusEnum.REJECTED);
        } else {
            return ApprovalBizHandleBo.error(ApprovalBizBatchWithdrawalStatusEnum.FAILED, message);
        }
    }
}
