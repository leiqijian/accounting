package com.liquido.worker.service;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PageShopifyDto;
import com.liquido.worker.pojo.dto.QueryShopifyDto;
import com.liquido.worker.pojo.vo.PageShopifyVo;
import com.liquido.worker.pojo.vo.QueryShopifyVo;

public interface ShopifyService {

    PageVo<PageShopifyDto> pageShopify(final PageShopifyVo vo);

    QueryShopifyDto queryShopify(final QueryShopifyVo vo);

}
