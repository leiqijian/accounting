package com.liquido.worker.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.worker.api.TaskFeeCalculationApi;
import com.liquido.worker.pojo.dto.PreCalculateFeeDto;
import com.liquido.worker.pojo.dto.TaskFeeCalculationDto;
import com.liquido.worker.pojo.vo.FillFieldTaskFeeCalculationVo;
import com.liquido.worker.pojo.vo.SyncHandleTaskFeeCalculationVo;
import com.liquido.worker.pojo.vo.SyncRerunTaskFeeCalculationVo;
import com.liquido.worker.pojo.vo.SyncTaskFeeCalculationVo;
import com.liquido.worker.pojo.vo.UnHoldingDocumentVo;
import com.liquido.worker.pojo.vo.UnHoldingTransactionVo;
import com.liquido.worker.service.TaskFeeCalculationService;
import com.liquido.worker.service.calculate.TaskTransactionService;
import com.liquido.worker.service.sync.TransactionSyncService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TaskFeeCalculationController implements TaskFeeCalculationApi {

    private final TaskTransactionService taskTransactionService;
    private final TransactionSyncService transactionSyncService;
    private final TaskFeeCalculationService taskFeeCalculationService;


    @Override
    @PostMapping("/worker/task-fee-calculation/query/{id}")
    public ResponseDto<TaskFeeCalculationDto> queryTaskFeeCalculation(
            @PathVariable @NotNull final Long id) {
        return ResponseDto.success(taskFeeCalculationService.findById(id));
    }

    @Override
    @PostMapping("/worker/task-fee-calculation/query/unique-id/{id}")
    public ResponseDto<TaskFeeCalculationDto> queryTaskFeeCalculationByUniqueId(
            @PathVariable @NotNull final String id) {
        return ResponseDto.success(taskFeeCalculationService.findByUniqueId(id));
    }

    @PostMapping("/worker/task-fee-calculation/sync")
    public ResponseDto<Void> sync(@RequestBody @Valid final SyncTaskFeeCalculationVo vo) {
        transactionSyncService.sync(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/worker/task-fee-calculation/calculate-fee/pre")
    public ResponseDto<List<PreCalculateFeeDto>> preCalculateFee(
            @RequestBody @Valid final SyncHandleTaskFeeCalculationVo vo) {
        return ResponseDto.success(taskTransactionService.preCalculateFee(vo));
    }

    @Override
    @PostMapping("/worker/task-fee-calculation/sync-handle")
    public ResponseDto<Void> syncHandle(
            @RequestBody @Valid final SyncHandleTaskFeeCalculationVo vo) {
        transactionSyncService.syncHandle(vo.getBos());
        return ResponseDto.success();
    }

    @PostMapping("/worker/task-fee-calculation/sync/rerun")
    public ResponseDto<Void> syncRerun(
            @RequestBody @Valid final SyncRerunTaskFeeCalculationVo vo) {
        transactionSyncService.syncRerun(vo);
        return ResponseDto.success();
    }

    /**
     * Cancel holding amount by documentIds
     * use TaskFeeCalculationController.cancelHoldingByTransactionIds
     *
     * @param order vo
     */
    @Deprecated
    @PostMapping("/worker/task-fee-calculation/holding-document/cancel")
    public ResponseDto<Void> cancelHoldingByDocumentIds(
            @RequestBody @Valid final UnHoldingDocumentVo order) {
        taskFeeCalculationService.cancelHoldingByDocumentIds(order);
        return ResponseDto.success();
    }

    /**
     * Cancel holding amount by transactionIds
     *
     * @param order vo
     */
    @Override
    @PostMapping("/worker/task-fee-calculation/holding-transaction/cancel")
    public ResponseDto<Void> cancelHoldingByTransactionIds(
            @RequestBody @Valid final UnHoldingTransactionVo order) {
        taskFeeCalculationService.cancelHoldingByTransactionIds(order);
        return ResponseDto.success();
    }

    /**
     * fill documentId and field payerCity and targetName into other
     */
    @Override
    @PostMapping("/worker/task-fee-calculation/field/fill")
    public ResponseDto<Void> fillTaskFeeCalculationField(
            @RequestBody @Valid final List<FillFieldTaskFeeCalculationVo> list) {
        taskFeeCalculationService.fillTaskFeeCalculationField(list);
        return ResponseDto.success();
    }
}
