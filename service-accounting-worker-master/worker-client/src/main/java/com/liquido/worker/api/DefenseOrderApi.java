package com.liquido.worker.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.DefenseOrderDto;
import com.liquido.worker.pojo.dto.SummaryDefenseOrderDto;
import com.liquido.worker.pojo.vo.AcceptDefenseOrderVo;
import com.liquido.worker.pojo.vo.DefenseOrderDefenseVo;
import com.liquido.worker.pojo.vo.PageDefenseOrderVo;
import com.liquido.worker.pojo.vo.QueryDefenseOrderVo;
import com.liquido.worker.pojo.vo.SolveDefenseOrderVo;
import com.liquido.worker.pojo.vo.SummaryDefenseOrderVo;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface DefenseOrderApi {

    @PostMapping("/worker/defense-order/summary")
    ResponseDto<SummaryDefenseOrderDto> defenseOrderSummary(
            @RequestBody @Valid final SummaryDefenseOrderVo vo);

    @PostMapping("/worker/defense-order/page")
    ResponseDto<PageVo<DefenseOrderDto>> defenseOrderPage(
            @RequestBody @Valid final PageDefenseOrderVo vo);

    @PostMapping("/worker/defense-order/query")
    ResponseDto<DefenseOrderDto> defenseOrderQuery(
            @RequestBody @Valid final QueryDefenseOrderVo vo);

    @PostMapping("/worker/defense-order/query-by-unique-id/{uniqueId}")
    ResponseDto<DefenseOrderDto> defenseOrderQueryByUniqueId(
            @PathVariable("uniqueId") String uniqueId);

    @PostMapping("/worker/defense-order/defense")
    ResponseDto<DefenseOrderDto> defenseOrderDefense(
            @RequestBody @Valid final DefenseOrderDefenseVo vo);

    @PostMapping("/worker/defense-order/solve")
    ResponseDto<Void> defenseOrderSolve(
            @RequestBody @Valid final SolveDefenseOrderVo vo);

    @PostMapping("/worker/defense-order/accept")
    ResponseDto<Void> defenseOrderAccept(
            @RequestBody @Valid final AcceptDefenseOrderVo vo);
}
