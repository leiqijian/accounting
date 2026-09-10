package com.liquido.worker.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PageShopifyDto;
import com.liquido.worker.pojo.dto.QueryShopifyDto;
import com.liquido.worker.pojo.vo.PageShopifyVo;
import com.liquido.worker.pojo.vo.QueryShopifyVo;
import com.liquido.worker.pojo.vo.SyncDataVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ShopifyApi {

    @PostMapping("/worker/shopify/sync")
    ResponseDto<Void> syncShopify(@RequestBody @Valid final SyncDataVo vo);

    @PostMapping("/worker/shopify/page")
    ResponseDto<PageVo<PageShopifyDto>> pageShopify(@RequestBody @Valid final PageShopifyVo vo);

    @PostMapping("/worker/shopify/query")
    ResponseDto<QueryShopifyDto> queryShopify(@RequestBody @Valid final QueryShopifyVo vo);

}
