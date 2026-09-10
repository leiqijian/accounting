package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.liquido.statement.pojo.bo.TransactionInProgressBo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAddTransactionProgressVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    @NotEmpty
    private List<TransactionInProgressBo> listVo;

}
