package com.liquido.transaction.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.transaction.pojo.dto.AppendixDto;
import com.liquido.transaction.pojo.vo.QueryAppendixVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AppendixApi {

    @PostMapping("/transaction/appendix/download-url/generator")
    ResponseDto<List<AppendixDto>> generatorAppendixDownloadUrl(
            @RequestBody @Valid final QueryAppendixVo vo);

}
