package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchTransactionMoneyVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String requestId;

    @NotNull
    private Long accountId;

    @NotNull
    private List<TransactionMoneyVo> transactionMoneyList;

}
