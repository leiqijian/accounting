package com.liquido.worker.service.sync;

import java.util.List;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.pojo.dto.DwSyncShopifyDto;
import com.liquido.worker.pojo.vo.SyncDataVo;

/**
 * Load transaction order from data warehouse
 */
public interface ShopifySyncService {

    /**
     * shopify data sync
     */
    void sync(final TransactionTypeCodeEnum typeCodeEnum);

    /**
     * shopify data sync
     */
    void sync(final SyncDataVo vo);

    /**
     * each of shopify data to judge , store and calculation
     *
     * @param dtoList shopify data
     */
    void syncHandle(final List<DwSyncShopifyDto> dtoList);

}
