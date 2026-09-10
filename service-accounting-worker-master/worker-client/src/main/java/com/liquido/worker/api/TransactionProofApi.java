package com.liquido.worker.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.worker.pojo.dto.QueryBrProofDto;
import com.liquido.worker.pojo.dto.QueryCoProofDto;
import com.liquido.worker.pojo.dto.QueryMxSpeiProofDto;
import com.liquido.worker.pojo.vo.QueryProofVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionProofApi {

    @PostMapping("/worker/transaction/proof/mx/spei/query")
    ResponseDto<QueryMxSpeiProofDto> queryMxSpeiProof(
            @RequestBody @Valid final QueryProofVo vo);

    @PostMapping("/worker/transaction/proof/br/query")
    ResponseDto<QueryBrProofDto> queryBrProof(@RequestBody @Valid final QueryProofVo vo);

    @PostMapping("/worker/transaction/proof/co/query")
    ResponseDto<QueryCoProofDto> queryCoProof(@RequestBody @Valid final QueryProofVo vo);

}
