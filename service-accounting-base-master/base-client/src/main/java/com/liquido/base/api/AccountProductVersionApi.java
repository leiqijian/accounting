package com.liquido.base.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.base.pojo.dto.AccountProductVersionDto;
import com.liquido.base.pojo.vo.AccountProductVersionVo;
import com.liquido.base.pojo.vo.EditAccountProductVersionMonthlyFlagVo;
import com.liquido.base.pojo.vo.QueryAccountProductVersionVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountProductVersionApi {

    @PostMapping("/base/account-product-version/add")
    ResponseDto<Long> addAccountProductVersion(@Valid @RequestBody AccountProductVersionVo vo);

    @PostMapping("/base/account-product-version/batch-add")
    ResponseDto<List<AccountProductVersionDto>> batchAddAccountProductVersion(
            @Valid @RequestBody List<AccountProductVersionVo> listVo);

    @PostMapping("/base/account-product-version/list")
    ResponseDto<List<AccountProductVersionDto>> listAccountProductVersion(
            @Valid @RequestBody QueryAccountProductVersionVo vo);

    @PostMapping("/base/account-product-version/monthly-flag/update")
    ResponseDto<Long> updateAccountProductVersionMonthFlag(
            @Valid @RequestBody EditAccountProductVersionMonthlyFlagVo vo);

}
