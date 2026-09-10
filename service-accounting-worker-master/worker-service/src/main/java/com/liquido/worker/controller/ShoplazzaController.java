package com.liquido.worker.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.api.ShoplazzaApi;
import com.liquido.worker.pojo.dto.PageShoplazzaDto;
import com.liquido.worker.pojo.dto.QueryShoplazzaDto;
import com.liquido.worker.pojo.vo.PageShoplazzaVo;
import com.liquido.worker.pojo.vo.QueryShoplazzaVo;
import com.liquido.worker.pojo.vo.SyncDataVo;
import com.liquido.worker.service.ShoplazzaService;
import com.liquido.worker.service.sync.ShoplazzaSyncService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class ShoplazzaController implements ShoplazzaApi {

    private final ShoplazzaSyncService shoplazzaSyncService;
    private final ShoplazzaService shoplazzaService;

    @Override
    @PostMapping("/worker/shoplazza/sync")
    public ResponseDto<Void> syncShoplazza(@RequestBody @Valid final SyncDataVo vo) {
        shoplazzaSyncService.sync(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/worker/shoplazza/page")
    public ResponseDto<PageVo<PageShoplazzaDto>> pageShoplazza(
            @RequestBody @Valid final PageShoplazzaVo vo) {
        return ResponseDto.success(shoplazzaService.pageShoplazza(vo));
    }

    @Override
    @PostMapping("/worker/shoplazza/query")
    public ResponseDto<QueryShoplazzaDto> queryShoplazza(
            @RequestBody @Valid final QueryShoplazzaVo vo) {
        return ResponseDto.success(shoplazzaService.queryShoplazza(vo));
    }

}
