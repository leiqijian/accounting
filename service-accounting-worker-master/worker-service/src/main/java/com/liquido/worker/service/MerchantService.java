package com.liquido.worker.service;


import com.liquido.worker.pojo.bo.MerchantAccountBo;

public interface MerchantService {

    MerchantAccountBo getMerchantAccountInfo(final String key);
}
