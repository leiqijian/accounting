package com.liquido.statement.service;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.GlobalStatementPageDto;
import com.liquido.statement.pojo.entity.GlobalStatement;
import com.liquido.statement.pojo.vo.QueryGlobalStatementPageVo;

public interface GlobalStatementService {

    void saveAccountFlow(final GlobalStatement statement);

    PageVo<GlobalStatementPageDto> queryGlobalStatementPage(final QueryGlobalStatementPageVo vo);
}
