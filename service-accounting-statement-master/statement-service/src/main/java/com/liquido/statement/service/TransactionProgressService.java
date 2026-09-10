package com.liquido.statement.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.liquido.statement.pojo.bo.SumTransactionInProgressNetAmountBo;
import com.liquido.statement.pojo.dto.TransactionInProgressDto;
import com.liquido.statement.pojo.entity.TransactionInProgress;
import com.liquido.statement.pojo.vo.BatchAddTransactionProgressVo;

public interface TransactionProgressService {

    List<TransactionInProgressDto> batchAddTransactionProgress(BatchAddTransactionProgressVo vo);

    TransactionInProgressDto queryByUniqueId(String uniqueId);

    List<SumTransactionInProgressNetAmountBo> sumSubAccountPendAndSettlementAmount(
            Collection<Long> accountIds, final Collection<String> subMerchantId,
            final TransactionInProgress inProgress);

    TransactionInProgress findFirstInProgressDataByAccountAndSubMerchant(final Set<Long> accountIds,
            final Set<String> subMerchantIds);
}
