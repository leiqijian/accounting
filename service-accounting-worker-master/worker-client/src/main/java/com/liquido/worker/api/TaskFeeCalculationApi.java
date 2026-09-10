package com.liquido.worker.api;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.worker.pojo.dto.PreCalculateFeeDto;
import com.liquido.worker.pojo.dto.TaskFeeCalculationDto;
import com.liquido.worker.pojo.vo.FillFieldTaskFeeCalculationVo;
import com.liquido.worker.pojo.vo.SyncHandleTaskFeeCalculationVo;
import com.liquido.worker.pojo.vo.UnHoldingDocumentVo;
import com.liquido.worker.pojo.vo.UnHoldingTransactionVo;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TaskFeeCalculationApi {

    @PostMapping("/worker/task-fee-calculation/query/{id}")
    ResponseDto<TaskFeeCalculationDto> queryTaskFeeCalculation(
            @PathVariable @NotNull final Long id);

    @PostMapping("/worker/task-fee-calculation/query/unique-id/{id}")
    ResponseDto<TaskFeeCalculationDto> queryTaskFeeCalculationByUniqueId(
            @PathVariable @NotNull final String id);

    @Deprecated
    @PostMapping("/worker/task-fee-calculation/holding-document/cancel")
    ResponseDto<Void> cancelHoldingByDocumentIds(
            @RequestBody @Valid final UnHoldingDocumentVo order);

    @PostMapping("/worker/task-fee-calculation/holding-transaction/cancel")
    ResponseDto<Void> cancelHoldingByTransactionIds(
            @RequestBody @Valid final UnHoldingTransactionVo order);

    @PostMapping("/worker/task-fee-calculation/field/fill")
    ResponseDto<Void> fillTaskFeeCalculationField(
            @RequestBody @Valid final List<FillFieldTaskFeeCalculationVo> list);


    @PostMapping("/worker/task-fee-calculation/calculate-fee/pre")
    ResponseDto<List<PreCalculateFeeDto>> preCalculateFee(
            @RequestBody @Valid final SyncHandleTaskFeeCalculationVo vo);

    @PostMapping("/worker/task-fee-calculation/sync-handle")
    ResponseDto<Void> syncHandle(
            @RequestBody @Valid final SyncHandleTaskFeeCalculationVo vo);
}
