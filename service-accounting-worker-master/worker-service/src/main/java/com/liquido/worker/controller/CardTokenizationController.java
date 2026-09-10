package com.liquido.worker.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.api.TokenizationApi;
import com.liquido.worker.pojo.dto.PageTokenizationDto;
import com.liquido.worker.pojo.vo.PageTokenizationVo;
import com.liquido.worker.pojo.vo.SyncDataVo;
import com.liquido.worker.service.TokenizationService;
import com.liquido.worker.service.sync.CardTokenizationSyncService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class CardTokenizationController implements TokenizationApi {

    private final TokenizationService tokenizationService;
    private final CardTokenizationSyncService cardTokenizationSyncService;

    @Override
    @PostMapping("/worker/tokenization/page")
    public ResponseDto<PageVo<PageTokenizationDto>> pageTokenization(
            @RequestBody @Valid final PageTokenizationVo vo) {
        return ResponseDto.success(tokenizationService.pageTokenization(vo));
    }

    @Override
    @PostMapping("/worker/tokenization/sync")
    public ResponseDto<Void> syncTokenization(@RequestBody @Valid final SyncDataVo vo) {
        cardTokenizationSyncService.sync(vo);
        return ResponseDto.success();
    }

}
