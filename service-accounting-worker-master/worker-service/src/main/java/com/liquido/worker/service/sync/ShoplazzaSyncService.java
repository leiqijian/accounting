package com.liquido.worker.service.sync;

import java.util.List;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.pojo.dto.DwSyncShoplazzaDto;
import com.liquido.worker.pojo.vo.SyncDataVo;

/**
 * Load transaction order from data warehouse
 */
public interface ShoplazzaSyncService {

    /**
     * shoplazza data sync
     */
    void sync(final TransactionTypeCodeEnum typeCodeEnum);

    /**
     * shoplazza data sync
     */
    void sync(final SyncDataVo vo);

    /**
     * each of shoplazza data to judge , store and calculation
     *
     * @param dtoList shoplazza data
     */
    void syncHandle(final List<DwSyncShoplazzaDto> dtoList);

}
