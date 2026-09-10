package com.liquido.base.controller;

import javax.validation.Valid;

import com.liquido.base.api.MerchantMessageApi;
import com.liquido.base.pojo.dto.PageMerchantMessageDto;
import com.liquido.base.pojo.vo.AddMerchantMessageVo;
import com.liquido.base.pojo.vo.PageMerchantMessageVo;
import com.liquido.base.service.MerchantMessageService;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MerchantMessageController implements MerchantMessageApi {

    private final MerchantMessageService merchantMessageService;

    @Override
    @PostMapping("/base/merchant-message/save")
    public ResponseDto<Void> saveMerchantMessage(
            @RequestBody @Valid final AddMerchantMessageVo vo) {
        merchantMessageService.saveMessage(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/base/merchant-message/page")
    public ResponseDto<PageVo<PageMerchantMessageDto>> pageMerchantMessage(
            @RequestBody @Valid final PageMerchantMessageVo vo) {
        return ResponseDto.success(merchantMessageService.pageMerchantMessage(vo));
    }

}
