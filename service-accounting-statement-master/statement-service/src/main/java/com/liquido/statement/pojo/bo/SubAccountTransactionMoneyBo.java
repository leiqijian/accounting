package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.liquido.statement.pojo.dto.TransactionMoneyDto;
import com.liquido.statement.pojo.entity.AccountStatement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountTransactionMoneyBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate localDate;

    private List<TransactionMoneyDto> transactionMoneyList;

    private List<AccountStatement> accountStatementList;
}
