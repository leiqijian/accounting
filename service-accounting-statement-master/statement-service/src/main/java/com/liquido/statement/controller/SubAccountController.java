package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.api.SubAccountApi;
import com.liquido.statement.pojo.dto.PageSubAccountDto;
import com.liquido.statement.pojo.dto.SubAccountDto;
import com.liquido.statement.pojo.vo.BatchAddSubAccountVo;
import com.liquido.statement.pojo.vo.ListSubAccountVo;
import com.liquido.statement.pojo.vo.PageSubAccountVo;
import com.liquido.statement.pojo.vo.QuerySubAccountVo;
import com.liquido.statement.service.SubAccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class SubAccountController implements SubAccountApi {

    private final SubAccountService subAccountService;

    @Override
    @PostMapping("/statement/sub-account/query")
    public ResponseDto<SubAccountDto> querySubAccount(
            @RequestBody @Valid final QuerySubAccountVo vo) {
        return ResponseDto.success(subAccountService.querySubAccount(vo));
    }

    @PostMapping("/statement/sub-account/list")
    public ResponseDto<List<SubAccountDto>> listSubAccount(
            @RequestBody @Valid final ListSubAccountVo vo) {
        return ResponseDto.success(subAccountService.listSubAccount(vo));
    }

    @Override
    @PostMapping("/statement/sub-account/page")
    public ResponseDto<PageVo<PageSubAccountDto>> pageSubAccount(
            @RequestBody @Valid final PageSubAccountVo vo) {
        return ResponseDto.success(subAccountService.pageSubAccount(vo));
    }

    @PostMapping("/statement/sub-account/batch-add")
    public ResponseDto<Void> batchAddSubAccount(@RequestBody @Valid final BatchAddSubAccountVo vo) {
        subAccountService.batchAddSubAccount(vo);
        return ResponseDto.success();
    }
}
