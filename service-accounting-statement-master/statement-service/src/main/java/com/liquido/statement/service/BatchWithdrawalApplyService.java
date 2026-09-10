package com.liquido.statement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.liquido.statement.pojo.entity.BatchWithdrawalApply;
import com.liquido.statement.pojo.entity.BatchWithdrawalDetail;
import com.liquido.statement.pojo.vo.ListBatchWithdrawalApplyVo;

public interface BatchWithdrawalApplyService {

    List<BatchWithdrawalApply> batchSave(
            final long batchId,
            final List<BatchWithdrawalDetail> dataList);

    List<BatchWithdrawalApply> queryApplyList(
            final ListBatchWithdrawalApplyVo vo);

    List<BatchWithdrawalApply> batchSave(final List<BatchWithdrawalApply> dataList);

    void batchUpdateStateProcessing(final List<Long> batchWithdrawalApplyIds);

    void batchUpdateFinishState(final LocalDateTime completeTime, final LocalDate completeDate,
                                final Long batchId, final Set<String> subMerchantId,
                                final int state);

}
