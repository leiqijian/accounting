package com.liquido.statement.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.GlobalAccountInfoDto;
import com.liquido.statement.pojo.dto.GlobalStatementPageDto;
import com.liquido.statement.pojo.vo.GlobalAccountTopupVo;
import com.liquido.statement.pojo.vo.QueryGlobalAccountVo;
import com.liquido.statement.pojo.vo.QueryGlobalStatementPageVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface GlobalAccountApi {

    @PostMapping("/statement/global/account/topup")
    ResponseDto<Void> topupGlobalAccount(@RequestBody @Valid final GlobalAccountTopupVo vo);

    @PostMapping("/statement/global/account/info/query")
    ResponseDto<GlobalAccountInfoDto> queryGlobalAccountInfo(
            @RequestBody @Valid final QueryGlobalAccountVo vo);

    @PostMapping("/statement/global/account/statement/page")
    ResponseDto<PageVo<GlobalStatementPageDto>> queryGlobalStatementPage(
            @RequestBody @Valid final QueryGlobalStatementPageVo vo);
}
