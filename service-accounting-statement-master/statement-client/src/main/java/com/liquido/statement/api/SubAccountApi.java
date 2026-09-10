package com.liquido.statement.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.PageSubAccountDto;
import com.liquido.statement.pojo.dto.SubAccountDto;
import com.liquido.statement.pojo.vo.ListSubAccountVo;
import com.liquido.statement.pojo.vo.PageSubAccountVo;
import com.liquido.statement.pojo.vo.QuerySubAccountVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SubAccountApi {

    @PostMapping("/statement/sub-account/query")
    ResponseDto<SubAccountDto> querySubAccount(
            @RequestBody @Valid final QuerySubAccountVo vo);

    @PostMapping("/statement/sub-account/list")
    ResponseDto<List<SubAccountDto>> listSubAccount(
            @RequestBody @Valid final ListSubAccountVo vo);


    @PostMapping("/statement/sub-account/page")
    ResponseDto<PageVo<PageSubAccountDto>> pageSubAccount(
            @RequestBody @Valid final PageSubAccountVo vo);
}
