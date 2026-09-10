package com.liquido.worker.service;

import java.util.List;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.DefenseOrderDto;
import com.liquido.worker.pojo.dto.SummaryDefenseOrderDto;
import com.liquido.worker.pojo.entity.DefenseOrder;
import com.liquido.worker.pojo.vo.AcceptDefenseOrderVo;
import com.liquido.worker.pojo.vo.DefenseOrderDefenseVo;
import com.liquido.worker.pojo.vo.PageDefenseOrderVo;
import com.liquido.worker.pojo.vo.QueryDefenseOrderVo;
import com.liquido.worker.pojo.vo.SolveDefenseOrderVo;
import com.liquido.worker.pojo.vo.SummaryDefenseOrderVo;

public interface DefenseOrderService {

    SummaryDefenseOrderDto defenseOrderSummary(final SummaryDefenseOrderVo vo);

    PageVo<DefenseOrderDto> defenseOrderPage(final PageDefenseOrderVo vo);

    DefenseOrderDto defenseOrderQuery(final QueryDefenseOrderVo vo);

    DefenseOrderDto defenseOrderQueryByUniqueId(final String uniqueId);

    DefenseOrder getById(final Long id);

    void save(final List<DefenseOrder> list);

    DefenseOrderDto defenseOrderDefense(DefenseOrderDefenseVo vo);

    void defenseOrderSolve(final SolveDefenseOrderVo vo);

    void defenseOrderAccept(final AcceptDefenseOrderVo vo);

}
