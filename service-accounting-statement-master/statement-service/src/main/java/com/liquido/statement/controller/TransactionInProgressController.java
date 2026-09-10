package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.TransactionInProgressApi;
import com.liquido.statement.manage.InProgressManager;
import com.liquido.statement.pojo.vo.TransactionInProgressVo;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionInProgressController implements TransactionInProgressApi {

    private final InProgressManager inProgressManager;

    @PostMapping("/statement/transaction/inProgress/sync")
    public ResponseDto<Void> syncTransactionInProgress(
            @RequestBody @Valid final List<TransactionInProgressVo> list) {
        inProgressManager.syncTransactionInProgress(list);
        return ResponseDto.success();
    }
}
