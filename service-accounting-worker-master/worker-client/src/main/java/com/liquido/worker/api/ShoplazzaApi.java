package com.liquido.worker.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PageShoplazzaDto;
import com.liquido.worker.pojo.dto.QueryShoplazzaDto;
import com.liquido.worker.pojo.vo.PageShoplazzaVo;
import com.liquido.worker.pojo.vo.QueryShoplazzaVo;
import com.liquido.worker.pojo.vo.SyncDataVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ShoplazzaApi {

    @PostMapping("/worker/shoplazza/sync")
    ResponseDto<Void> syncShoplazza(@RequestBody @Valid final SyncDataVo vo);

    @PostMapping("/worker/shoplazza/page")
    ResponseDto<PageVo<PageShoplazzaDto>> pageShoplazza(
            @RequestBody @Valid final PageShoplazzaVo vo);

    @PostMapping("/worker/shoplazza/query")
    ResponseDto<QueryShoplazzaDto> queryShoplazza(@RequestBody @Valid final QueryShoplazzaVo vo);

}
