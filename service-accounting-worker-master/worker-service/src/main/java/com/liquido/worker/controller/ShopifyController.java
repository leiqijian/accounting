package com.liquido.worker.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.api.ShopifyApi;
import com.liquido.worker.pojo.dto.PageShopifyDto;
import com.liquido.worker.pojo.dto.QueryShopifyDto;
import com.liquido.worker.pojo.vo.PageShopifyVo;
import com.liquido.worker.pojo.vo.QueryShopifyVo;
import com.liquido.worker.pojo.vo.SyncDataVo;
import com.liquido.worker.service.ShopifyService;
import com.liquido.worker.service.sync.ShopifySyncService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class ShopifyController implements ShopifyApi {

    private final ShopifySyncService shopifySyncService;
    private final ShopifyService shopifyService;

    @Override
    @PostMapping("/worker/shopify/sync")
    public ResponseDto<Void> syncShopify(@RequestBody @Valid final SyncDataVo vo) {
        shopifySyncService.sync(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/worker/shopify/page")
    public ResponseDto<PageVo<PageShopifyDto>> pageShopify(
            @RequestBody @Valid final PageShopifyVo vo) {
        return ResponseDto.success(shopifyService.pageShopify(vo));
    }

    @Override
    @PostMapping("/worker/shopify/query")
    public ResponseDto<QueryShopifyDto> queryShopify(@RequestBody @Valid final QueryShopifyVo vo) {
        return ResponseDto.success(shopifyService.queryShopify(vo));
    }

}
