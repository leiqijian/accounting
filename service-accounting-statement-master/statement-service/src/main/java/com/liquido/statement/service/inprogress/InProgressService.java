package com.liquido.statement.service.inprogress;

import java.util.List;

import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.vo.TransactionInProgressVo;

public interface InProgressService {

    void processing(final List<TransactionInProgressVo> transactionInProgressVoList,
                    final AccountDto account);
}
