package com.liquido.transaction.events.handle.batchwithdrawal;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DealershipWithdrawalInfoDto;
import com.liquido.statement.pojo.vo.BatchWithdrawalApprovedHandleVo;
import com.liquido.statement.pojo.vo.ListBatchWithdrawalApplyVo;
import com.liquido.statement.pojo.vo.QueryAccountVo;
import com.liquido.transaction.common.Constant;
import com.liquido.transaction.enums.ApprovalBizBatchWithdrawalStatusEnum;
import com.liquido.transaction.enums.ApprovalNodeTypeEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.exception.TransactionExceptionCode;
import com.liquido.transaction.pojo.bo.ApprovalBizHandleBo;
import com.liquido.transaction.pojo.bo.BatchWithdrawalExcelDataBo;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizBatchWithdrawal;
import com.liquido.transaction.pojo.entity.ApprovalNode;
import com.liquido.transaction.service.AppendixService;
import com.liquido.transaction.service.ApprovalBizBatchWithdrawalService;
import com.liquido.transaction.service.ApprovalNodeService;
import com.liquido.transaction.service.monitor.LarkRobotMonitor;

import com.alibaba.excel.EasyExcel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalBizBatchWithdrawalCompleteHandle
        implements BaseApprovalBizBatchWithdrawalHandle {

    private final StatementApis.StatementFeign statementFeign;
    private final ApprovalNodeService approvalNodeService;
    private final AppendixService appendixService;
    private final LarkRobotMonitor larkRobotMonitor;
    private final ApprovalBizBatchWithdrawalService approvalBizBatchWithdrawalService;

    @Override
    public boolean isSupport(final Approval approval,
                             final ApprovalBizBatchWithdrawal batchWithdrawal) {
        return ApprovalStatusEnum.COMPLETED == approval.getStatus()
                && ApprovalBizBatchWithdrawalStatusEnum.COMPLETED != batchWithdrawal.getStatus();
    }

    @Override
    public ApprovalBizHandleBo<ApprovalBizBatchWithdrawalStatusEnum> handle(
            final Approval approval,
            final ApprovalBizBatchWithdrawal batchWithdrawal) {

        log.info("batch withdrawal complete event={}", batchWithdrawal);

        boolean executeResult = false;
        String message;

        try {
            // ready for data
            final AccountDto accountDto = CheckResponseUtil.checkAndReturnResponseData(
                    statementFeign.getEffectiveAccountInfo(QueryAccountVo.builder()
                            .id(batchWithdrawal.getAccountId()).build()));

            final List<DealershipWithdrawalInfoDto> dtoList = statementFeign.listWithdrawalApply(
                    ListBatchWithdrawalApplyVo.builder()
                            .batchId(batchWithdrawal.getBatchId()).build()).getData();

            final Map<String, DealershipWithdrawalInfoDto> dtoMap = dtoList.stream().collect(
                    Collectors.toMap(DealershipWithdrawalInfoDto::getSubMerchantId, obj -> obj));

            final ApprovalNode node = approvalNodeService.findApprovalLastNode(approval.getId());
            if (!ApprovalNodeTypeEnum.END.equals(node.getNodeType())
                    || CollectionUtils.isEmpty(node.getAppendixIds())) {
                log.error("batch withdrawal approval node exception, last node error: {}",
                        batchWithdrawal.getApproval().getId());
                throw TransactionExceptionCode.LARK_APPROVAL_ERROR.exception(
                        "The final node has no attachment.");
            }
            final Long appendixId = node.getAppendixIds().stream().findFirst().get();

            // parse excel file and data
            final BatchWithdrawalApprovedHandleVo vo =
                    parseExcelFileAndDate(appendixId, batchWithdrawal, dtoMap);

            if (Objects.nonNull(vo)) {
                final ResponseDto<Void> responseDto =
                        statementFeign.batchWithdrawalApprovedHandle(vo);
                executeResult = responseDto.isSuccess();
                message = responseDto.getMsg();
            } else {
                executeResult = true;
                message = "request manual intervention";
            }

            if (!executeResult) {
                log.error("batch withdrawal, statement request error, "
                                + "withdrawal failed, errorMessage:{}, ID: {}", message,
                        batchWithdrawal.getId());
            }
            final LocalDateTime completeTime = LocalDateTimeUtil.nowUtc();
            batchWithdrawal.setCompletedTime(completeTime);
            batchWithdrawal.setCompletedDate(completeTime.atZone(LocalDateTimeUtil.UTC_ZONE)
                    .withZoneSameInstant(ZoneId.of(accountDto.getTimezone())).toLocalDate());
            batchWithdrawal.setFailCount(Constant.BIZ_APPROVAL.ZERO_COUNT);
        } catch (Exception e) {
            log.error("batch withdrawal approval error, error: ", e);
            message = e.getMessage();
        }

        return executeResult
                ? ApprovalBizHandleBo.success(ApprovalBizBatchWithdrawalStatusEnum.COMPLETED)
                : ApprovalBizHandleBo.error(ApprovalBizBatchWithdrawalStatusEnum.FAILED, message);

    }

    private List<BatchWithdrawalExcelDataBo> parseExcelFile(String filePath) {
        try {
            return EasyExcel.read(filePath)
                    .head(BatchWithdrawalExcelDataBo.class)
                    .sheet()
                    .doReadSync();
        } catch (Exception e) {
            log.error("parse excel failed: {}", e.getMessage(), e);
            throw TransactionExceptionCode.FILE_GENERATE_FAILED.exception();
        }
    }

    private BatchWithdrawalApprovedHandleVo parseExcelFileAndDate(
            final Long appendixId,
            final ApprovalBizBatchWithdrawal batchWithdrawal,
            final Map<String, DealershipWithdrawalInfoDto> dtoMap) {

        try {
            log.info("try to parse appendix, appendixId: {}", appendixId);
            final String filePath = String.format("temp-withdrawal-result-%s.xlsx", appendixId);
            appendixService.downloadFile(appendixId, filePath);
            final List<BatchWithdrawalExcelDataBo> boList = parseExcelFile(filePath);

            approvalBizBatchWithdrawalService.validExcelBatchWithdrawalData(boList, dtoMap);

            final BatchWithdrawalApprovedHandleVo vo =
                    approvalBizBatchWithdrawalService.parseExcelData(
                            batchWithdrawal, boList, dtoMap);

            Files.delete(Paths.get(filePath));
            return vo;
        } catch (Exception e) {
            // something wrong happened at parsing file or data,
            // alert and request manual intervention,
            // avoid blocking the approval flow and make the status to complete.
            log.error("Batch Withdrawal Parse File Or Data Error", e);
            final String title = "Batch Withdrawal Parse File Or Data Error";
            larkRobotMonitor.error(title, e.getMessage(), null);
            return null;
        }
    }
}
