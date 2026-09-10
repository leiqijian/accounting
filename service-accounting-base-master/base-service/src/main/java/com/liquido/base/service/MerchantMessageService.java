package com.liquido.base.service;


import com.liquido.base.pojo.dto.PageMerchantMessageDto;
import com.liquido.base.pojo.vo.AddMerchantMessageVo;
import com.liquido.base.pojo.vo.PageMerchantMessageVo;
import com.liquido.core.mvc.vo.PageVo;

public interface MerchantMessageService {

    void saveMessage(AddMerchantMessageVo vo);

    PageVo<PageMerchantMessageDto> pageMerchantMessage(PageMerchantMessageVo vo);

}
