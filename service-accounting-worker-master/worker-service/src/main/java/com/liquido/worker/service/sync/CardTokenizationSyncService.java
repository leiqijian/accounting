package com.liquido.worker.service.sync;

import java.util.List;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.pojo.dto.DwSyncTokenizationDto;
import com.liquido.worker.pojo.vo.SyncDataVo;

/**
 * Load transaction order from data warehouse
 */
public interface CardTokenizationSyncService {

    /**
     * payment link data sync
     */
    void sync(final TransactionTypeCodeEnum typeCodeEnum);

    /**
     * payment link data sync
     */
    void sync(final SyncDataVo vo);

    /**
     * each of tokenization data to judge , store and calculation
     *
     * @param dtoList tokenization data
     */
    void syncHandle(final List<DwSyncTokenizationDto> dtoList);

}
