package com.liquido.worker.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.worker.api.TransactionProofApi;
import com.liquido.worker.pojo.dto.QueryBrProofDto;
import com.liquido.worker.pojo.dto.QueryCoProofDto;
import com.liquido.worker.pojo.dto.QueryMxSpeiProofDto;
import com.liquido.worker.pojo.vo.QueryProofVo;
import com.liquido.worker.service.TransactionProofService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionProofController implements TransactionProofApi {

    private final TransactionProofService transactionProofService;

    @Override
    @PostMapping("/worker/transaction/proof/mx/spei/query")
    public ResponseDto<QueryMxSpeiProofDto> queryMxSpeiProof(
            @RequestBody @Valid final QueryProofVo vo) {
        return ResponseDto.success(transactionProofService.queryMxSpeiProof(vo));
    }

    @Override
    @PostMapping("/worker/transaction/proof/br/query")
    public ResponseDto<QueryBrProofDto> queryBrProof(
            @RequestBody @Valid final QueryProofVo vo) {
        return ResponseDto.success(transactionProofService.queryBrProof(vo));
    }

    @Override
    @PostMapping("/worker/transaction/proof/co/query")
    public ResponseDto<QueryCoProofDto> queryCoProof(
            @RequestBody @Valid final QueryProofVo vo) {
        return ResponseDto.success(transactionProofService.queryCoProof(vo));
    }

}
