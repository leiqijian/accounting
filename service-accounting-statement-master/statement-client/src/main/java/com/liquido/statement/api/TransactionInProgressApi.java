package com.liquido.statement.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.vo.TransactionInProgressVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionInProgressApi {

    @PostMapping("/statement/transaction/inProgress/sync")
    ResponseDto<Void> syncTransactionInProgress(
            @RequestBody @Valid final List<TransactionInProgressVo> list);
}
