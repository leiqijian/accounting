package com.liquido.transaction.service;

import java.util.List;

import com.liquido.transaction.pojo.vo.UpdateMerchantBalanceVo;


public interface AccountService {

    List<UpdateMerchantBalanceVo.MerchantBalanceVo> uploadBalanceToTrade();

}
