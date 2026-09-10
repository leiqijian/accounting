package com.liquido.statement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.statement.pojo.entity.BatchWithdrawalDetail;
import com.liquido.statement.pojo.vo.ListBatchWithdrawalDetailVo;

public interface BatchWithdrawalDetailService {

    List<BatchWithdrawalDetail> batchSave(
            final List<BatchWithdrawalDetail> entityList);

    boolean idempotentCheck(
            final Long transactionId,
            final DirectionTypeEnum directionType,
            final Long accountId);

    List<BatchWithdrawalDetail> list(final ListBatchWithdrawalDetailVo vo);

    void batchUpdateStateProcessing(final Long batchId);

    void batchUpdateFinishState(final LocalDateTime completeTime, final LocalDate completeDate,
                                final Long batchId, final Set<String> subMerchantId,
                                final int state);
}
