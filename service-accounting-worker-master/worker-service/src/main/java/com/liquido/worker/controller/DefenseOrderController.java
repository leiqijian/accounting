package com.liquido.worker.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.api.DefenseOrderApi;
import com.liquido.worker.pojo.dto.DefenseOrderDto;
import com.liquido.worker.pojo.dto.SummaryDefenseOrderDto;
import com.liquido.worker.pojo.vo.AcceptDefenseOrderVo;
import com.liquido.worker.pojo.vo.DefenseOrderDefenseVo;
import com.liquido.worker.pojo.vo.PageDefenseOrderVo;
import com.liquido.worker.pojo.vo.QueryDefenseOrderVo;
import com.liquido.worker.pojo.vo.SolveDefenseOrderVo;
import com.liquido.worker.pojo.vo.SummaryDefenseOrderVo;
import com.liquido.worker.service.DefenseOrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class DefenseOrderController implements DefenseOrderApi {

    private final DefenseOrderService defenseOrderService;

    @Override
    @PostMapping("/worker/defense-order/summary")
    public ResponseDto<SummaryDefenseOrderDto> defenseOrderSummary(
            @RequestBody @Valid final SummaryDefenseOrderVo vo) {
        return ResponseDto.success(defenseOrderService.defenseOrderSummary(vo));
    }

    @Override
    @PostMapping("/worker/defense-order/page")
    public ResponseDto<PageVo<DefenseOrderDto>> defenseOrderPage(
            @RequestBody @Valid final PageDefenseOrderVo vo) {
        return ResponseDto.success(defenseOrderService.defenseOrderPage(vo));
    }

    @Override
    @PostMapping("/worker/defense-order/query")
    public ResponseDto<DefenseOrderDto> defenseOrderQuery(
            @RequestBody @Valid final QueryDefenseOrderVo vo) {
        return ResponseDto.success(defenseOrderService.defenseOrderQuery(vo));
    }

    @Override
    @PostMapping("/worker/defense-order/query-by-unique-id/{uniqueId}")
    public ResponseDto<DefenseOrderDto> defenseOrderQueryByUniqueId(
            @PathVariable("uniqueId") String uniqueId) {
        return ResponseDto.success(defenseOrderService.defenseOrderQueryByUniqueId(uniqueId));
    }

    @Override
    @PostMapping("/worker/defense-order/defense")
    public ResponseDto<DefenseOrderDto> defenseOrderDefense(
            @RequestBody @Valid final DefenseOrderDefenseVo vo) {
        return ResponseDto.success(defenseOrderService.defenseOrderDefense(vo));
    }

    @Override
    @PostMapping("/worker/defense-order/solve")
    public ResponseDto<Void> defenseOrderSolve(
            @RequestBody @Valid final SolveDefenseOrderVo vo) {
        defenseOrderService.defenseOrderSolve(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/worker/defense-order/accept")
    public ResponseDto<Void> defenseOrderAccept(
            @RequestBody @Valid final AcceptDefenseOrderVo vo) {
        defenseOrderService.defenseOrderAccept(vo);
        return ResponseDto.success();
    }

}
