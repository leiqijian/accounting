package com.liquido.base.controller;

import java.util.List;

import com.liquido.base.api.AccountProductVersionApi;
import com.liquido.base.pojo.dto.AccountProductVersionDto;
import com.liquido.base.pojo.vo.AccountProductVersionVo;
import com.liquido.base.pojo.vo.EditAccountProductVersionMonthlyFlagVo;
import com.liquido.base.pojo.vo.QueryAccountProductVersionVo;
import com.liquido.base.service.AccountProductVersionService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class AccountProductVersionController implements AccountProductVersionApi {

    private final AccountProductVersionService accountProductVersionService;

    @Override
    @PostMapping("/base/account-product-version/add")
    public ResponseDto<Long> addAccountProductVersion(final AccountProductVersionVo vo) {
        final Long id = accountProductVersionService.save(vo);
        return ResponseDto.success(id);
    }

    @Override
    @PostMapping("/base/account-product-version/batch-add")
    public ResponseDto<List<AccountProductVersionDto>> batchAddAccountProductVersion(
            final List<AccountProductVersionVo> listVo) {
        return ResponseDto.success(accountProductVersionService.saveAll(listVo));
    }

    @Override
    @PostMapping("/base/account-product-version/list")
    public ResponseDto<List<AccountProductVersionDto>> listAccountProductVersion(
            final QueryAccountProductVersionVo vo) {
        return ResponseDto.success(accountProductVersionService.listAccountProductVersion(vo));
    }

    @Override
    @PostMapping("/base/account-product-version/monthly-flag/update")
    public ResponseDto<Long> updateAccountProductVersionMonthFlag(
            final EditAccountProductVersionMonthlyFlagVo vo) {
        return ResponseDto.success(accountProductVersionService
                .updateAccountProductVersionMonthFlag(vo));
    }

}
