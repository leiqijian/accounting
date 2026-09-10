package com.liquido.worker.service;

import com.liquido.worker.pojo.dto.QueryBrProofDto;
import com.liquido.worker.pojo.dto.QueryCoProofDto;
import com.liquido.worker.pojo.dto.QueryMxSpeiProofDto;
import com.liquido.worker.pojo.vo.QueryProofVo;


public interface TransactionProofService {

    QueryMxSpeiProofDto queryMxSpeiProof(final QueryProofVo vo);

    QueryBrProofDto queryBrProof(final QueryProofVo vo);

    QueryCoProofDto queryCoProof(final QueryProofVo vo);

}
