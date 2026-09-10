package com.liquido.worker.service;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PageTokenizationDto;
import com.liquido.worker.pojo.vo.PageTokenizationVo;

public interface TokenizationService {

    PageVo<PageTokenizationDto> pageTokenization(final PageTokenizationVo vo);

}
