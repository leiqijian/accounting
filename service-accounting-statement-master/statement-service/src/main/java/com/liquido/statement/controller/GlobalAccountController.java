package com.liquido.statement.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.api.GlobalAccountApi;
import com.liquido.statement.pojo.dto.GlobalAccountInfoDto;
import com.liquido.statement.pojo.dto.GlobalStatementPageDto;
import com.liquido.statement.pojo.vo.GlobalAccountTopupVo;
import com.liquido.statement.pojo.vo.QueryGlobalAccountVo;
import com.liquido.statement.pojo.vo.QueryGlobalStatementPageVo;
import com.liquido.statement.service.GlobalAccountService;
import com.liquido.statement.service.GlobalStatementService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class GlobalAccountController implements GlobalAccountApi {
    private final GlobalAccountService globalAccountService;
    private final GlobalStatementService globalStatementService;

    @Override
    @PostMapping("/statement/global/account/info/query")
    public ResponseDto<GlobalAccountInfoDto> queryGlobalAccountInfo(
            @RequestBody @Valid final QueryGlobalAccountVo vo) {
        return ResponseDto.success(
                globalAccountService.queryGlobalAccountInfo(vo.getMerchantId()));
    }

    @Override
    @PostMapping("/statement/global/account/topup")
    public ResponseDto<Void> topupGlobalAccount(
            @RequestBody @Valid final GlobalAccountTopupVo vo) {
        globalAccountService.topupGlobalAccount(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/global/account/statement/page")
    public ResponseDto<PageVo<GlobalStatementPageDto>> queryGlobalStatementPage(
            @RequestBody @Valid final QueryGlobalStatementPageVo vo) {
        return ResponseDto.success(globalStatementService.queryGlobalStatementPage(vo));
    }
}
