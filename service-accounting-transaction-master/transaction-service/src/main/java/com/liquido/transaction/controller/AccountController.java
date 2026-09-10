package com.liquido.transaction.controller;

import java.util.List;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.transaction.pojo.vo.UpdateMerchantBalanceVo;
import com.liquido.transaction.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/transaction/account/balance/upload/query")
    public ResponseDto<List<UpdateMerchantBalanceVo.MerchantBalanceVo>> queryUploadAccountBalance() {
        return ResponseDto.success(accountService.uploadBalanceToTrade());
    }

}
