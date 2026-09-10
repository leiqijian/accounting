package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.statement.enums.TransactionChargeBackStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolveTransactionChargeBackOrderVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private String uniqueId;

    @NotNull
    @Convert(converter = TransactionChargeBackStatusEnum.Convert.class)
    private TransactionChargeBackStatusEnum status;

    @NotBlank
    @Length(max = 400)
    private String result;

}
