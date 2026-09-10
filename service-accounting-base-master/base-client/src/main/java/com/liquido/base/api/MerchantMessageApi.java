package com.liquido.base.api;

import javax.validation.Valid;

import com.liquido.base.pojo.dto.PageMerchantMessageDto;
import com.liquido.base.pojo.vo.AddMerchantMessageVo;
import com.liquido.base.pojo.vo.PageMerchantMessageVo;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MerchantMessageApi {

    @PostMapping("/base/merchant-message/save")
    ResponseDto<Void> saveMerchantMessage(@RequestBody @Valid final AddMerchantMessageVo vo);

    @PostMapping("/base/merchant-message/page")
    ResponseDto<PageVo<PageMerchantMessageDto>> pageMerchantMessage(
            @RequestBody @Valid final PageMerchantMessageVo vo);

}
