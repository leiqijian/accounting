package com.liquido.worker.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PageTokenizationDto;
import com.liquido.worker.pojo.vo.PageTokenizationVo;
import com.liquido.worker.pojo.vo.SyncDataVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TokenizationApi {

    @PostMapping("/worker/tokenization/page")
    ResponseDto<PageVo<PageTokenizationDto>> pageTokenization(
            @RequestBody @Valid final PageTokenizationVo vo);

    @PostMapping("/worker/tokenization/sync")
    ResponseDto<Void> syncTokenization(@RequestBody @Valid final SyncDataVo vo);

}
