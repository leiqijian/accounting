package com.liquido.worker.service.sync;

import java.util.List;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.pojo.dto.DwSyncPaymentLinkDto;
import com.liquido.worker.pojo.vo.SyncDataVo;

/**
 * Load transaction order from data warehouse
 */
public interface PaymentLinkSyncService {

    /**
     * payment link data sync
     */
    void sync(final TransactionTypeCodeEnum typeCodeEnum);

    /**
     * payment link data sync
     */
    void sync(final SyncDataVo vo);

    /**
     * each of payment link data to judge , store and calculation
     *
     * @param dtoList payment link data
     */
    void syncHandle(final List<DwSyncPaymentLinkDto> dtoList);

}
