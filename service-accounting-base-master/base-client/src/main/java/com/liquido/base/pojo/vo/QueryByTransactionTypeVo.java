package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryByTransactionTypeVo implements Serializable {
    private static final long serialVersionUID = 1;

    @NotNull
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionType;
}
