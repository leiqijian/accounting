package com.liquido.transaction.service;

import java.util.List;
import java.util.Map;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.DealershipWithdrawalInfoDto;
import com.liquido.statement.pojo.vo.BatchWithdrawalApprovedHandleVo;
import com.liquido.transaction.pojo.bo.BatchWithdrawalExcelDataBo;
import com.liquido.transaction.pojo.dto.ApprovalBizBatchWithdrawalDto;
import com.liquido.transaction.pojo.dto.PageApprovalBizBatchWithdrawalDto;
import com.liquido.transaction.pojo.entity.ApprovalBizBatchWithdrawal;
import com.liquido.transaction.pojo.vo.CreateApprovalBizBatchWithdrawalVo;
import com.liquido.transaction.pojo.vo.CreateBatchWithdrawalApprovalVo;
import com.liquido.transaction.pojo.vo.PageApprovalBizBatchWithdrawalVo;
import com.liquido.transaction.pojo.vo.QueryApprovalBizBatchWithdrawalVo;

import org.springframework.web.multipart.MultipartFile;

public interface ApprovalBizBatchWithdrawalService {

    ApprovalBizBatchWithdrawalDto createApprovalBizBatchWithdrawal(
            final CreateApprovalBizBatchWithdrawalVo vo);

    ApprovalBizBatchWithdrawalDto createApprovalBizBatchWithdrawal(
            final CreateBatchWithdrawalApprovalVo vo);

    ApprovalBizBatchWithdrawal findByApprovalId(final Long approvalId);

    ApprovalBizBatchWithdrawal findByBatchId(final Long batchId);

    void validExcelBatchWithdrawalData(final List<BatchWithdrawalExcelDataBo> boList,
                                       final Map<String, DealershipWithdrawalInfoDto> dtoMap);

    BatchWithdrawalApprovedHandleVo parseExcelData(
            final ApprovalBizBatchWithdrawal batchWithdrawal,
            final List<BatchWithdrawalExcelDataBo> boList,
            final Map<String, DealershipWithdrawalInfoDto> dtoMap);

    ApprovalBizBatchWithdrawalDto queryApprovalBizBatchWithdrawal(
            final QueryApprovalBizBatchWithdrawalVo vo);

    PageVo<PageApprovalBizBatchWithdrawalDto> pageApprovalBizBatchWithdrawal(
            final PageApprovalBizBatchWithdrawalVo vo);

    void retryUnfrozenAmount(final MultipartFile file, final Long batchId);

}
