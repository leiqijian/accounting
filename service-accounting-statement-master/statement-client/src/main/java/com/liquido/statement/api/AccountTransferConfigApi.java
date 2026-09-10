package com.liquido.statement.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.AccountTransferConfigDto;
import com.liquido.statement.pojo.vo.ModifyAccountTransferConfigStateVo;
import com.liquido.statement.pojo.vo.QueryAccountTransferConfigPageVo;
import com.liquido.statement.pojo.vo.SaveAccountTransferConfigVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountTransferConfigApi {

    @PostMapping("/statement/account/internal/auto/transfer/config/save")
    ResponseDto<Void> saveAccountAutoTransferConfig(
            @RequestBody @Valid final SaveAccountTransferConfigVo vo);

    @PostMapping("/statement/account/internal/auto/transfer/config/page")
    ResponseDto<PageVo<AccountTransferConfigDto>> queryAccountAutoTransferConfigPage(
            @RequestBody @Valid final QueryAccountTransferConfigPageVo vo);

    @PostMapping("/statement/account/internal/auto/transfer/config/state/switch")
    ResponseDto<Void> updateAccountAutoTransferConfigState(
            @RequestBody @Valid final ModifyAccountTransferConfigStateVo vo);
}
