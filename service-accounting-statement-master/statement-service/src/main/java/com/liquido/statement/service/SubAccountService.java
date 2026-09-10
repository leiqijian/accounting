package com.liquido.statement.service;

import java.util.List;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.PageSubAccountDto;
import com.liquido.statement.pojo.dto.SubAccountDto;
import com.liquido.statement.pojo.vo.BatchAddSubAccountVo;
import com.liquido.statement.pojo.vo.ListSubAccountVo;
import com.liquido.statement.pojo.vo.PageSubAccountVo;
import com.liquido.statement.pojo.vo.QuerySubAccountVo;

public interface SubAccountService {

    SubAccountDto querySubAccount(QuerySubAccountVo vo);

    List<SubAccountDto> listSubAccount(ListSubAccountVo vo);

    PageVo<PageSubAccountDto> pageSubAccount(PageSubAccountVo vo);

    void batchAddSubAccount(BatchAddSubAccountVo vo);
}
