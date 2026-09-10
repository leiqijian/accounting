package com.liquido.worker.service.sync;

import java.util.List;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.pojo.dto.DwSyncTransactionDto;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.vo.SyncRerunTaskFeeCalculationVo;
import com.liquido.worker.pojo.vo.SyncTaskFeeCalculationVo;

/**
 * Load transaction order from data warehouse
 */
public interface TransactionSyncService {

    /**
     * transaction data sync
     */
    void sync(final TransactionTypeCodeEnum typeCodeEnum);

    /**
     * transaction data sync
     */
    void sync(final SyncTaskFeeCalculationVo vo);

    /**
     * each of transaction data to judge , store and calculation
     *
     * @param bos transaction data
     */
    void syncHandle(final List<DwSyncTransactionDto> bos);

    /**
     * transaction fee calculate run again with db data
     *
     * @param vo params
     */
    void syncRerun(final SyncRerunTaskFeeCalculationVo vo);

    void updateShownInfo(final TaskFeeCalculation dbData,
                         final DwSyncTransactionDto syncData);

}
