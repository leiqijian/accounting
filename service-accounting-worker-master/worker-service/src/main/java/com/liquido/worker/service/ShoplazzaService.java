package com.liquido.worker.service;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PageShoplazzaDto;
import com.liquido.worker.pojo.dto.QueryShoplazzaDto;
import com.liquido.worker.pojo.vo.PageShoplazzaVo;
import com.liquido.worker.pojo.vo.QueryShoplazzaVo;

public interface ShoplazzaService {

    PageVo<PageShoplazzaDto> pageShoplazza(final PageShoplazzaVo vo);

    QueryShoplazzaDto queryShoplazza(final QueryShoplazzaVo vo);

}
